# Notes about the project

## The team

- Hassan KESKIN: hassan.keskin@icloud.com
- Hakan KESKIN: hakan.keskin@icloud.com

## Comments
###A naive scheduler to start

Pour cette première partie nous savions pas vraiment comment commencer. La diffusion par M.Hermnier du code de la première partie a été pour nous d'une grande aide. En effet, cela nous a permit de mieu comprendre comment interagir avec cloudsim et avoir les bases pour créer un scheduler.
Nous nous sommes donc basé sur ce code afin de développer les schedulers suivants.

###Support for Highly-Available applications

* AntiAffinity Scheduller

Pour ce scheduller (`antiAffinity`) le point important est de ne pas créer dans un même host des Vms dont l'ID est compris entre [0-99], [100-199] etc..
La solution ici, est donc de vérifier dans chaque host, si les Vms présent apartiennent au même groupe que la VM a allouer. (Il suffit de faire une division par 100 dans une variable int afin d'obtenir un identificateur commun à tous les vms compris dans le même groupe).
Afin de réaliser cela au niveau code, nous avons déclarer un booleen `antiAffinity` qui est initialisé à `true` par defaut. Si pendant le parcourt de la liste, il y a présence d'une VM ayant un id du même groupe que la VM courant alors on attribue au boolean la valeur `false` et la vm n'est pas créer dans l'host courant.
On attribue donc la valeur `true` à l'allocation de la vm seulement si la variable `antiAffinity`=`true` et si la vm s'est bien créer dans l'host courant.

* AntiAffinity Observer