# ocp3-rental

## Description

`ocp3-rental` est une application (API) Spring Boot qui permet de gérer des locations. Elle utilise une base de données MySQL pour stocker les données et gère l'authentification à l'aide de JWT (JSON Web Tokens).

## Installation

Pour installer le projet, suivez les étapes suivantes :
1. Clonez ce dépôt de code sur votre machine locale.
2. Assurez-vous d'avoir Java installé sur votre système.
```bash
git clone https://github.com/DiD-BDX/OCP3-RentalAPI.git
cd ocp3-rental
mvn install
```
3. Exécutez la commande `mvn install` pour installer les dépendances.
4. Configurez les informations de connexion à la base de données dans le fichier `application.properties`.
5. Configurer la clef de sécurité dans le fichier `application.properties`.
6. Exécutez l'application en utilisant la commande `mvn spring-boot:run`.

## Technologies utilisées

- Java
- Spring Boot
- MySQL
- HTML/CSS
- Bootstrap
- Pour la documentation de l'API: Swagger (OpenApi)

## Utilisation de l'API
Une fois l'application lancée, aller sur http://localhost:3001/swagger-ui/index.html
(dans le cas ou le port Tomcat defini est 3001)

## Auteur

- [Didier Barriere Doleac](https://github.com/DiD-BDX)

## Licence

Ce projet est sous licence MIT. Veuillez consulter le fichier `LICENSE` pour plus d'informations.