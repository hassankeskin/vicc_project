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
La solution ici, est donc de vérifier dans chaque host, si les Vms présent apartiennent au même groupe que la VM à allouer. (Il suffit de faire une division par 100 dans une variable int afin d'obtenir un identificateur commun à tous les vms compris dans le même groupe).
Afin de réaliser cela au niveau code, nous avons déclaré un boolean `antiAffinity` qui est initialisé à `true` par defaut. Si pendant le parcourt de la liste, il y a présence d'une VM ayant un id du même groupe que la VM courant, alors on attribue au boolean la valeur `false` et la vm n'est pas créer dans l'host courant.
On attribue donc la valeur `true` à l'allocation de la vm seulement si la variable `antiAffinity`=`true` et si la vm s'est bien crée dans l'host courant.

* AntiAffinity Observer

Pour l'Observer le point important est de parcourir tout les hosts et de comparer entre elles chaque VMs. 
Afin de faire des test pour le fonctionnement nous avons fait appel au scheduller de base (`naive`) pour la création des VMs.

Pour la création de la classe, nous nous sommes basés sur `PeakPowerObserver`. L'objet pour cette classe était instancié dans le Main mais nous avons préferé d'instancier l'objet `AntiAffinityObserver` dans la classe dédié au "build" des observers.
Le builder (de la classe `Observers.java`) n'étant surement pas encore testé, il manquait le parametre `List<PowerHost>`  permettant de faire passer la listes des hosts en parametre lors de l'instanciation du builder dans le Main. 
Nous avons donc ajouté ce dernier dans la methode `build` de la class `Observers.java`.

Concernant le code de l'observer, nous parcouront tous les hosts et comparont les VMs entre elles. Si 2 Vms appartenant au même groupe est detecté, nous envoyons un erreur dans le fichier Log.

###Balance the load

* Balance load Scheduller

On reprent la même structure que les autres Class (`AntiAffinityVmAllocationPolicy` et `BalanceLoadVmAllocationPolicy` ).
Pour ce scheduller (`balanceLoad`) le point important est d'implémenter l'équillibrage de charge. Pour ce faire, en suivant vos conseils, nous avons choisie de creer un scheduleur qui va placer les vm sur une hôte ayant le meilleure MIPS (`meilleureHote`).
Les premieres vm seront placé sur les Hôtes ayant le plus gros MIPS et ainsi de suite. Nous aurons au final une repartition des charges equilibrées.
Concretement, dans une boucle `for` parcourant la liste d'Hote, nous gardons dans la variable `meilleureHote` l'Hote ayant le MIPS max (le meilleure MIPS etant stocké dans la variable `meilleureMips` ).


* Balance load Observer

Nous reprenons la même trame que l'Observer du PeakPower. Comme "ID" de l'atribut `OBSERVE` nous avons choisie "555556".
comme vous nous l'avez expliqué, pour calculer le taux de MIPS moyen disponible, nous prenons le MIPS de l'Hote ayant le MIPS le plus grand `mipsMaximum` et nous la soustrayons au MIPS le plus petit `mipsMinimum`.
Cette difference est stocker dans la variable `mipsRange`.
Le taux (`ctauxMips`) est calculé avec un produit en croix:
 - `ctauxMips` =  (`mipsRange`/`mipsMinimum`)*100
 Ce taux est donc calculé toute les secondes.
 
###Get rid of SLA violations

* noViolations Scheduller

Pour ce scheduller (`noViolations`) le but est de coder un scheduller créant de VMs de tel sorte à ne pas avoir un limitation de MIPS coté hosts lorsque la VM demande des MIPS.
Pour cela il a suffit de créer la VM, seulement sur des hosts dont le MIPS restant est supérrieur à la VM que nous voulons allouer.
