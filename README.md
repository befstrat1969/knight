The app was developed using Kotlin 1.4.32 using Android Studio 4.1.2

The ViewModel approach using LiveData was used for UI managment and coroutines for background processing. The chessboard was implemented as a custom View control.

To use the app select chessboard size and number of moves. Then tap on the chessboard to select the starting (Knight) and ending (King) position.
Press "Calculate" to start calculation, "Cancel" to stop calculation and "Reset" to reset the board.
Changing grid size or number of moves also stops calculation if in progress. Changing grid size also resets the board
The results are presented in a list using algebraic chess notation and are updated during calculation. The number of found results is also shown.
Tapping on a result draws its path on the chessboard

