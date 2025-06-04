Informatii generale despre proiect:

- Proiectul este o aplicatie de Destktop ce imita aplicatiile de desktop a paginilor web precum Friv sau Y8;
- Acesta poate fi asemanat si cu platforme mai mari precum Steam sau Epic Games prin modul in care va functiona;
- Jocurile ce trebuie implementate neaparat sunt TicTacToe (X si 0) si Minesweeper;
- TicTacToe va fi un joc multiplayer, in care un jucator va putea crea un "room/lobby", iar celalalt da join pe baza unui ID;
- Minesweeperva fi un joc singleplayer, in care jucatorul va putea incepe o runda noua, juca, salva, da load, sau iesi; 

Informatii utile:

- Pentru arhitectura acestui proiect, vom folosi o modificare adusa clasicului MVC;
- Pentru Baza de Date vom folosi PostreSQl, adica vom avea o baza de date relationara;
- Vom folosi JPA pentru a comunica intre baza de date si proiectul nostru;
- Interfata va fi realizata in JavaFX, folosind SceneBuilder; 
- Pentru partea de networking vom folosi protocolul TCP-IP, avand conexiune realizata prin socket-uri;
- Pentru debugging folosim loggers, in \src\main\java\org\example\game_library avem folderul de utils, acolo se afla o configurare pentru loggers;
- Acestia sunt afisati atat in terminal, cat salvati si in format fizic in folderul \Game_Library\logs ce va fi creat in calculatorul tau atunci cand rulezi prima data codul;
- Cat timp serverul sau clientul sunt pornite, tu vei vedea in folderul de logs si fisiere .lck, acestea sunt fisiere de tip lock, ignora-le;
