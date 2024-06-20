# SAE_Application_repartie
LETONDAL_JALLAIS_MONNIER-BLONDEAU_TOUBHANS

Etape 1 : -	Sur une machine A, dans un terminal du dossier ./src/Service lancer la commande rmiregistry afin de lancer l’annuaire 
Etape 2 : -	Dans une base de données Oracle SQLDeveloper, lancer le script « database.sql » dans la racine du projet afin de créer les schémas de données, ainsi que d’ajouter un jeu de données de test. 
Etape 3 : -	Lancer dans un autre terminal de la machine A dans le dossier ./src/Service lancer la classe LancerService.
            -	java -cp '.;ojdbc11.jar' LancerService identifiantOracle mdpOracle 
Etape 4 : -	Sur une machine B, dans un terminal dans le dossier src/main/java du projet lancer la classe LancerProxy <port>. java LancerProxy 8080 
Etape 5 : -	Pour tester avec 3 machines différentes, modifiez le fichier « index.js » et modifiez la variable ligne 12 « urlProxy » et remplacez « localhost » par l’adresse IP de la machine B.
          -	Lancer sur une machine C, en local, le fichier « index.html » dans un navigateur. 
Etape 6 : -	Sinon, pour lancer depuis webetu, lancez le Proxy depuis la machine B et depuis cette même machine, cliquez sur le lien suivant pour utiliser l’application : https://webetu.iutnc.univ-lorraine.fr/~letondal3u/SAE_Application_repartie/ Pour pouvoir accéder au proxy depuis webetu, il est important que le Proxy soit lancé sur la même machine que celle qui exécute l’application, car si on tente de fetch une adresse IP externe, il nous oblige à que la requête soit en « https », et nous n’avons pas eu le temps de configurer le serveur pour qu’il puisse accepter les requêtes https. L’application hébergée sur webetu effectue donc un fetch vers http://localhost:8080, qui est autorisée en non https car ça ne change pas de domaine.