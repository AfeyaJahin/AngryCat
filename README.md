# AngyCats
AngyCats is a modified version of the original flappy bird game built using java and javaFX

Run Instructions:
1. Use the command git clone git-clone-link to obtain a local copy of the code onto your computer.
2. Git clone link: https://github.com/MHC-FA21-CS225/angryflappybird-team-angycats.git
3. Open angryflappybird-team-angycats on Eclipse
4. Go on Run Configurations > Java Application
5. Project: angryflappybird-team-angycats
6. Main class: AngryFlappyBird
7. Click Run

Press Go! buttion on the game screen to start the game. Player presses the GUI Go! button to move the angry cat in a scrolling scene. The cat has to avoid obstacles (including pipes, rats and floor) and move through the pipes without collision while collecting as many fish as possible along the way to prevent the rats from stealing the fishes. Any time the cat goes through a pair of pipes without collision, 1 point is added; when the cat collects a yarn ball, 5 points are added ; when the bird collects a rare fish, a 6 second snooze time is granted, which means that the cat is under autopilot mode without the need for any button clicks to keep the cat flying or avoid obstacles. If a rat successfully stole a yarn ball, there is a 3 point deduction to the accumulative points.

There are three difficulty levels. The number of presses required to make the cat fly increases with difficulty getting harder making it harder to dodge the pipes and making it more likely to hit obstacles.

The angry cat has a total of three lives, if the cat hits a pipe, one life is taken, but the accumulative score is not reset, therefore the player can continue a new game until all three lives are taken, in which case the game is over. However, if a bird hits either the floor or the rat then the game is over.

