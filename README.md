# ocp3-rental

## Description

`ocp3-rental` est une application (API) Spring Boot qui permet de gérer des locations. Elle utilise une base de données MySQL pour stocker les données et gère l'authentification à l'aide de JWT (JSON Web Tokens).

## Installation

Pour installer le projet, suivez les étapes suivantes :
1. Clonez ce dépôt de code sur votre machine locale.
2. Assurez-vous d'avoir Java installé sur votre système.
```bash
git clone https://github.com/DiD-BDX/OCP3-RentalAPI.git
```
3. Ouvrez une invite de commante (PC) ou une fenetre de terminal 5(Mac) dans le dossier local du projet.
4. Exécutez la commande `mvn install` pour installer les dépendances.
```
mvn install
```
5. Exécutez la commande `mvn install` pour installer les dépendances.
6. Installez [mysql](https://dev.mysql.com/downloads/installer/) pour installer la base de donnée.
7. Depuis une invite de commande ou une fenetre de terminal, créez la base de donnée et importez les tables depuis le fichier script.sql contenu dans le package (/main/resources/SQL/script/sql).
```bash
CREATE DATABASE nom_de_votre_database
USE nom_de_votre_database
source /chemin_du_fichier/script.sql
```
8. Configurez les informations de connexion à la base de données dans des variables d'environnement (DB_USERNAME et DB_PASSWORD).

    Mac (terminal Bash):
    ```
    echo 'export DB_USERNAME=votre_login' >> ~/.bash_profile
    echo 'export DB_PASSWORD=votre_password' >> ~/.bash_profile
    ```
    PC (invite de commande):
    ```
    setx DB_USERNAME "votre_login"
    setx DB_PASSWORD "votre_password"
    ```
9. Configurer la clef de sécurité dans une variable d'environnement (JWT_KEY).

    Mac (terminal Bash): 
    ```
    echo 'export JWT_KEY=votre_clef_de_securite_256k' >> ~/.bash_profile
    ```
    PC (invite de commande):
    ```
    setx JWT_KEY "votre_clef_de_securite_256k"
    ```
10. Exécutez l'application en utilisant la commande `mvn spring-boot:run`.
    ```
    mvn spring-boot:run
    ```

## Technologies utilisées

- Java
- Spring Boot
- MySQL
- Pour la documentation de l'API: Swagger (OpenApi)

## Utilisation de l'API
Une fois l'application lancée, aller sur http://localhost:3001/swagger-ui/index.html
(dans le cas ou le port Tomcat defini dans application.property est 3001).

Pour obtenir un token jwt valide et tester l'API, utiliser [Postman](https://www.postman.com/), faite un "Register user" et en retour, vous aurez un token valide que vous pouvez copier et coller pour tester les autres routes de l'API.

## Auteur

- [Didier Barriere Doleac](https://github.com/DiD-BDX)

## Licence

Ce projet est sous licence MIT. Veuillez consulter le fichier `LICENSE` pour plus d'informations.