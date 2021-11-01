# Incity_lift_project
Research project (TIPE CPGE MP 2021 France)

Features:
-simulate a set of lift (potentially multiple elevator shafts in a collumn) in a building given a java algorithm and a set of individual person input.
-render the simulation through an animated GUI in order to finetune and debug the programs.
[lifts](https://user-images.githubusercontent.com/61628044/139741086-b543c7b8-6058-42b5-9ec3-85738ca4370e.png)
-generate a set of individual person input given a statistical profile.
-collision between elevators are avoided through the simulator / any physically impossible order given by the elevator algorithm throws an error.
-compute batch simulations for performance analysis on multiple threads.

performance:
around 80k person simulated per second per thread (on AMD ryzen 5 labtop) with a nearest neighbour algorithm.!

