=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: cshaw1
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Network IO: Enables remote connection of clients. This is an appropriate use of Network IO since I made
  websockets to facilitate the connection of clients.

  2. File IO: Allows for loading different custom game boards. FileIO is also used in my game to autosave the current
  state of the game every 10 seconds. This makes a accidentally quitting the game or a network disconnection less disruptive.

  3. Inheritance/Subtyping: Inheritance is necessary to store the state of the board correctly. There are different types
  of spaces on the board and they are no traversed by type, so an interface that all spaces implement is needed for my game.
  I specifically created a Space interface that BlankSpace and PropertySpace inherit from.

  4. JUnit Testable Component: The Player, PropertySpace, and Board are all tested through JUnit test cases. This ensures that
  attempts to change the state of the game are handled correctly, which is important for a game with as complex a state as Monopoly.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
 Monopoly: Entrypoint of game. Displays instructions to user, then creates a thread for the server and two separate
 threads for the clients.
 MonopolyMultiServer: Continuously checks for connections to ServerSocket and creates a MonopolyServerThread to handle
 each new connection.
 MonopolyServerThread: Handles requests from Client to change game state. Responds appropriately with an updated state.
 MonopolyClient: Facilitates connection to server. Processes responses from server and call clientGUI.update() to
 display state changes to user.
 ClientGUI: Main file for the GUI the client sees when playing the game. Uses a BorderLayout to hold buttons and display information, with a BoardGUI in the center.
 BoardGUI: Custom JPanel that displays the monopoly board.
 ClientMessage: a class representing the message sent by the client to the server for communication.
 ServerMessage: a class representing the message sent by the server to the client for communication.
 FileHandler: utility class with static functions to handle saving and recovering state and loading board.
 PropertyResponse: Enum of possible responses on attempts to update a PropertySpace (buying it, mortgaging it, etc.)
 SpacePrompt: Enum of possible responses after dice has rolled (user has paid rent, user went bankrupt, etc.)
 MonopolyState, PlayerState, PropertyState: Represent parts of the Board, Players, and Property that can change throughout the game.
 They are annotated so that Jackson can convert them to/from JSON.
 Space, BlankSpace, PropertySpace: Represent a space in the game. The Space interface requires implementers to provide a getName and landPlayer method.
 The BlankSpace does not affect the player in any way. PropertySpaces can be owned by a Player, have a varying number of houses, and may cost the player money if landed on.
 Player: Represents a player. Can only have an id of 1 or 2. Keeps track of how much money the player has.
 Board: Facilitates moving the player around the board and rolling dice.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
I found it difficult to implement the BoardGUI, especially the text on the board. I also had no experience
with websockets, but got it working in the end.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
I think it's a good separation of functionality. If I had more time I would have encapsulated the state of classes better,
especially the classes in the game package.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

https://stackoverflow.com/questions/41111870/swing-drawstring-text-bounds-and-line-wrapping
https://docs.oracle.com/javase/tutorial/
https://stackoverflow.com/questions/7814089/how-to-schedule-a-periodic-task-in-java
