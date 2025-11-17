 // Jenkinsfile (declarative) - projet-workspace
pipeline {
  agent any

  parameters {
    choice(name: 'ENV', choices: ['dev','staging','prod'], description: 'Environment to deploy')
    string(name: 'VERSION', defaultValue: '', description: 'Version tag to use (ex: v1.2.0). If empty, commit short SHA will be used')
    booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip unit tests?')
  }

  environment {
    REGISTRY = 'docker.io/stegueks27'              // change to your registry
    IMAGE_NAME = "${env.REGISTRY}/projet-workspace"
    DOCKERHUB_CREDS = credentials('dockerhub-creds') // id in Jenkins Credentials
    NEXUS_CREDS = credentials('nexus-creds')
    SONAR_TOKEN = credentials('sonar-token')
    KUBECONFIG_CREDENTIAL_ID = 'kubeconfig-prod'    // configure Jenkins with kubeconfig as secret file/credential
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        script { 
          // compute default VERSION if empty
          if (!params.VERSION) {
            env.VERSION = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          } else {
            env.VERSION = params.VERSION
          }
          echo "VERSION = ${env.VERSION}"
        }
      }
    }

    stage('Build - Backend') {
      steps {
        dir('demo') {
          sh "mvn -B clean package ${params.SKIP_TESTS ? '-DskipTests' : ''}"
        }
        archiveArtifacts artifacts: 'demo/target/*.jar', allowEmptyArchive: true
      }
    }

    stage('Build - Frontend') {
      steps {
        dir('angular-frontend') {
          sh '''
            npm ci
            npm run test -- --watch=false --browsers=ChromeHeadless --single-run || true
            npm run build -- --configuration=${ENV == 'prod' ? 'production' : 'development'}
          '''
        }
        archiveArtifacts artifacts: 'angular-frontend/dist/**', allowEmptyArchive: true
      }
    }

    stage('SonarQube Analysis') {
      when { expression { return env.SONAR_TOKEN != null && env.SONAR_TOKEN != '' } }
      steps {
        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          // assume sonar properties in project or adjust command
          sh "mvn -f demo sonar:sonar -Dsonar.login=${SONAR_TOKEN} || echo 'Sonar step failed or not configured'"
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          def tag = "${env.VERSION}"
          sh """
            export DOCKER_BUILDKIT=1
            docker build -t ${IMAGE_NAME}:${tag} .
            docker tag ${IMAGE_NAME}:${tag} ${IMAGE_NAME}:latest
          """
        }
      }
    }

    stage('Push Docker Image') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PSW')]) {
            sh '''
              echo "$DOCKER_PSW" | docker login -u "$DOCKER_USER" --password-stdin
              docker push ${IMAGE_NAME}:${VERSION}
              docker push ${IMAGE_NAME}:latest || true
              docker logout
            '''
          }
        }
      }
    }

    stage('Publish artifact to Nexus (prod only)') {
      when { expression { params.ENV == 'prod' } }
      steps {
        withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUSR', passwordVariable: 'NEXUSP')]) {
          dir('demo') {
            sh '''
              mvn -DskipTests deploy -DaltDeploymentRepository=nexus::default::https://nexus.example.com/repository/maven-releases/ \
                -Dnexus.username=$NEXUSR -Dnexus.password=$NEXUSP || echo "Nexus deploy skipped/failed"
            '''
          }
        }
      }
    }

    stage('Deploy to target environment') {
      steps {
        script {
          if (params.ENV == 'dev') {
            // local/dev less used for Jenkins; included for completeness
            sh "docker compose -f docker/docker-compose.dev.yml up -d --build || true"
          } else {
            // Use kubeconfig credential (stored as secret file)
            withCredentials([file(credentialsId: "${KUBECONFIG_CREDENTIAL_ID}", variable: 'KUBECONFIG_FILE')]) {
              sh """
                export KUBECONFIG=${KUBECONFIG_FILE}
                # replace VERSION in manifests using envsubst or yq
                mkdir -p k8s_rendered
                envsubst < k8s/${params.ENV}/deployment.yml > k8s_rendered/deployment.yml
                envsubst < k8s/${params.ENV}/service.yml > k8s_rendered/service.yml || true
                kubectl apply -f k8s_rendered/ -n ${params.ENV} --record
                kubectl rollout status deployment/projet-backend -n ${params.ENV} --timeout=120s || (kubectl describe deployment/projet-backend -n ${params.ENV} ; kubectl logs deploy/projet-backend -n ${params.ENV} --tail=200)
              """
            }
          }
        }
      }
    }

    stage('Run automated tests (staging)') {
      when { expression { params.ENV == 'staging' } }
      steps {
        script {
          // Trigger the selenium-tests job with parameter (XRAY key or TEST_EXEC)
          build job: 'selenium-tests-job', parameters: [
            string(name: 'TARGET_ENV', value: 'staging'),
            string(name: 'XRAY_TEST_EXEC_KEY', value: '') // leave empty to let test job choose or pass a value
          ], wait: true
        }
      }
    }

    stage('Manual Approval before prod') {
      when { expression { params.ENV == 'prod' } }
      steps {
        input message: "Approve deployment to PRODUCTION for version ${env.VERSION} ?", ok: "Deploy"
      }
    }

    stage('Post-Deployment checks (prod)') {
      when { expression { params.ENV == 'prod' } }
      steps {
        withCredentials([file(credentialsId: "${KUBECONFIG_CREDENTIAL_ID}", variable: 'KUBECONFIG_FILE')]) {
          sh """
            export KUBECONFIG=${KUBECONFIG_FILE}
            kubectl rollout status deployment/projet-backend -n prod --timeout=180s
            kubectl get pods -n prod -l app=projet-backend -o wide
            # optional smoke tests
            curl -fS http://prod.example.com/actuator/health || (echo 'healthcheck failed' ; exit 1)
          """
        }
      }
    }
  }

  post {
    success {
      echo "Pipeline finished SUCCESS for ${params.ENV} version ${env.VERSION}"
      // notify via email/slack if needed
    }
    failure {
      echo "Pipeline FAILED"
      // send failure notification
    }
  }
}
