# 🚀 Projet Employé - Backend Spring Boot

Image Docker pour l’application backend Spring Boot du projet de gestion des employés.

---

## 🧪 Technologies
- Java 21
- Spring Boot
- PostgreSQL
- Docker

---

## 🛠️ Variables d’environnement à définir

| Variable                | Description                        | Exemple                                       |
|------------------------|------------------------------------|-----------------------------------------------|
| `SPRING_DATASOURCE_URL`| URL JDBC vers la base de données   | `jdbc:postgresql://my-postgres:5432/EmployeeBD` |
| `POSTGRES_DB`          | Nom de la base PostgreSQL          | `EmployeeBD`                                  |
| `POSTGRES_USER`        | Utilisateur PostgreSQL             | `postgres`                                    |
| `POSTGRES_PASSWORD`    | Mot de passe PostgreSQL            | `root`                                        |

---

## ▶️ Exemple d'utilisation

### Avec `docker run`

```bash
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://my-postgres:5432/EmployeeBD \
  -e POSTGRES_DB=EmployeeBD \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=root \
  -p 8080:8080 \
  monutilisateur/backend:latest
