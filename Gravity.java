import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enigma.console.TextAttributes;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class Gravity {
    public static TextAttributes cyan = new TextAttributes(Color.cyan);
    public static TextAttributes white = new TextAttributes(Color.white);
    public static TextAttributes Blue = new TextAttributes(Color.blue);
    public static TextAttributes gray = new TextAttributes(Color.GRAY);
    public static TextAttributes green = new TextAttributes(Color.GREEN);
    public static TextAttributes magenta = new TextAttributes(Color.MAGENTA);
    public static TextAttributes Red = new TextAttributes(Color.red);
    public static TextAttributes Yellow = new TextAttributes(Color.yellow);
    public static enigma.console.Console cn = Enigma.getConsole("Gravity", 100, 35, 22, 0);
    public TextMouseListener tmlis;
    public KeyListener klis;

    // ------ Standard variables for mouse and keyboard ------
    public int mousepr;          // mouse pressed?
    public int mousex, mousey;   // mouse text coords.
    public int keypr;   // key pressed?
    public int rkey;    // key   (for press/release)
    // ----------------------------------------------------



    Random random = new Random();
    int boulderCount = 0;
    int treasureCount = 0;

    int treasure1C = 0;
    int treasure2C = 0;
    int treasure3C = 0;
    int emptyCount = 0;
    static int robotCount = 0;
    Stack s1 = new Stack(1000);
    Stack backpack = new Stack(8);
    int backpackCounter = -1;
    static char[][] map = new char[25][55];
    int px = 0, py = 0;

    int score = 0;
    int teleport = 3;
    static int time = 0;

    int inputQueueSize = 15;
    static int[][] robots = new int[100][2];
    boolean gameisover = false;
    Player player = new Player();

    CircularQueue inputQueue = new CircularQueue(inputQueueSize);

    Gravity() throws Exception {   // --- Contructor

        if (gameisover == false) {
            Ses.play("oyun.wav");

        }


        // ------ Standard code for mouse and keyboard ------ Do not change
        tmlis = new TextMouseListener() {
            public void mouseClicked(TextMouseEvent arg0) {
            }

            public void mousePressed(TextMouseEvent arg0) {
                if (mousepr == 0) {
                    mousepr = 1;
                    mousex = arg0.getX();
                    mousey = arg0.getY();
                }
            }

            public void mouseReleased(TextMouseEvent arg0) {
            }
        };
        cn.getTextWindow().addTextMouseListener(tmlis);

        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (keypr == 0) {
                    keypr = 1;
                    rkey = e.getKeyCode();
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        };
        cn.getTextWindow().addKeyListener(klis);
        // ----------------------------------------------------


        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 55; j++) {
                if (i == 0 || i == 24 || j == 0 || j == 54) { // 2 satır 2 sütun ile kare bir alan oluşturuyor
                    map[i][j] = '#';
                } else if (i == 8 && j < 50) { // oyun alanı içindeki labirent olmasını sağlayan duvardan biri
                    map[i][j] = '#';
                } else if (i == 16 && j > 4) {  // oyun alanı içindeki labirent olmasını sağlayan duvardan biri
                    map[i][j] = '#';
                } else {
                    map[i][j] = ' ';  // kalan kısımlar için (duvar harici)
                }

                if (map[i][j] == ' ') { //Game init - 2) all empty squares to earth squares
                    map[i][j] = ':';
                }


                cn.getTextWindow().output(j, i, map[i][j]); // bunu c#'daki setcursor gibi düşünebilirsiniz burada hizalamak için bunu kullanıyoruz
            }
        }

        while (boulderCount < 180) // Game init - 3) Random 180 earth squares to boulder
        {
            int boulder1 = random.nextInt(24 + 1);
            int boulder2 = random.nextInt(54 + 1);
            if (map[boulder1][boulder2] == ':') {

                map[boulder1][boulder2] = 'O';

                boulderCount++;
                cn.getTextWindow().output(boulder2, boulder1, map[boulder1][boulder2], green);
            }
            if (boulderCount == 180)
                break;
        }

        while (treasure1C <= 10) // Game init - 4) Random 10 earth squares to treasure 1
        {
            int treasureRow = random.nextInt(24 + 1);
            int treasureColumn = random.nextInt(54 + 1);

            if (map[treasureRow][treasureColumn] == ':') {

                map[treasureRow][treasureColumn] = '1';
                treasure1C++;
                cn.getTextWindow().output(treasureColumn, treasureRow, map[treasureRow][treasureColumn], cyan);
            }

            if (treasure1C == 10)
                break;
        }

        while (treasure2C <= 10) // Game init - 4) Random 10 earth squares to treasure 2
        {
            int treasureRow = random.nextInt(24 + 1);
            int treasureColumn = random.nextInt(54 + 1);

            if (map[treasureRow][treasureColumn] == ':') {

                map[treasureRow][treasureColumn] = '2';
                treasure2C++;
                cn.getTextWindow().output(treasureColumn, treasureRow, map[treasureRow][treasureColumn], cyan);
            }

            if (treasure2C == 10)
                break;
        }

        while (treasure3C <= 10) // Game init - 4) Random 10 earth squares to treasure 3
        {
            int treasureRow = random.nextInt(24 + 1);
            int treasureColumn = random.nextInt(54 + 1);

            if (map[treasureRow][treasureColumn] == ':') {

                map[treasureRow][treasureColumn] = '3';
                treasure3C++;
                cn.getTextWindow().output(treasureColumn, treasureRow, map[treasureRow][treasureColumn], cyan);

            }

            if (treasure3C == 10)
                break;
        }

        while (emptyCount <= 200)  // Game init - 5) Random 200 earth square to empty
        {
            int emptyRow = random.nextInt(24 + 1);
            int emptyColumn = random.nextInt(54 + 1);

            if (map[emptyRow][emptyColumn] == ':') {

                map[emptyRow][emptyColumn] = ' ';
                emptyCount++;

                cn.getTextWindow().output(emptyColumn, emptyRow, map[emptyRow][emptyColumn]);
            }

            if (emptyCount == 200)
                break;
        }


        while (robotCount <= 7) {
            int robotRow = random.nextInt(24 + 1);
            int robotColumn = random.nextInt(54 + 1);

            if (map[robotRow][robotColumn] == ':') {

                map[robotRow][robotColumn] = 'X';
                robots[robotCount][0] = robotRow;
                robots[robotCount][1] = robotColumn;
                robotCount++;

                cn.getTextWindow().output(robotColumn, robotRow, map[robotRow][robotColumn], Red);
            }

            if (robotCount == 7)
                break;

        } // Game init - 6) Random 7 earth square to robots


        while (map[py][px] != ':') {
            py = random.nextInt(24) + 1;
            px = random.nextInt(54) + 1;
        }
        map[py][px] = 'P';
        cn.getTextWindow().output(px, py, 'P');


        // Backpack section
        for (int i = 0; i < 8; i++) {
            cn.getTextWindow().output(61, 5 + i, '|');
            cn.getTextWindow().output(65, 5 + i, '|');
        }
        cn.getTextWindow().setCursorPosition(61, 13);
        cn.getTextWindow().output("+---+");
        cn.getTextWindow().setCursorPosition(60, 14);
        cn.getTextWindow().output("Backpack");


        while (!gameisover) { // esas oyun döngüsü

            if (keypr == 1) {    // if keyboard button pressed

                if (teleport > 0 && rkey == KeyEvent.VK_SPACE && map[py][px - 1] != '#' && map[py][px - 1] != 'X') {
                    while (true) {
                        int x, y;
                        x = random.nextInt(54);
                        y = random.nextInt(24);
                        if (map[y][x] == ' ') {
                            cn.getTextWindow().output(px, py, ' ');
                            px = x;
                            py = y;
                            teleport--;
                            break;

                        }
                    }
                }


                if (rkey == KeyEvent.VK_LEFT && map[py][px - 1] != '#' && map[py][px - 1] != 'X') {
                    try {
                        if (map[py][px - 1] == 'O') {
                            if (map[py][px - 2] == ' ') {
                                map[py][px - 2] = 'O';
                                cn.getTextWindow().output(px - 2, py, 'O', green);
                                map[py][px - 1] = ' ';
                                map[py][px] = ' ';
                                cn.getTextWindow().output(px, py, ' ');
                                px--;
                            }
                        } else {
                            map[py][px] = ' ';
                            cn.getTextWindow().output(px, py, ' ');
                            px--;
                        }

                    } catch (Exception e) {

                    }
                }


                if (rkey == KeyEvent.VK_RIGHT && map[py][px + 1] != '#' && map[py][px + 1] != 'X') {
                    try {
                        if (map[py][px + 1] == 'O') {
                            if (map[py][px + 2] == ' ') {
                                map[py][px + 2] = 'O';
                                cn.getTextWindow().output(px + 2, py, 'O', green);
                                map[py][px + 1] = ' ';
                                map[py][px] = ' ';
                                cn.getTextWindow().output(px, py, ' ');
                                px++;
                            }
                        } else {
                            map[py][px] = ' ';
                            cn.getTextWindow().output(px, py, ' ');
                            px++;
                        }

                    } catch (Exception e) {

                    }
                }
                if (rkey == KeyEvent.VK_UP && map[py - 1][px] != '#' && map[py - 1][px] != 'O' && map[py - 1][px] != 'X') {
                    map[py][px] = ' ';
                    cn.getTextWindow().output(px, py, ' ');
                    py--;
                }
                if (rkey == KeyEvent.VK_DOWN && map[py + 1][px] != '#' && map[py + 1][px] != 'O' && map[py + 1][px] != 'X') {
                    map[py][px] = ' ';
                    cn.getTextWindow().output(px, py, ' '); // yazdırma kısımları
                    if (map[py - 1][px] == 'O') {
                        gameisover = true;
                        cn.getTextWindow().setCursorPosition(59, 27);
                        cn.getTextWindow().output("No escape. Game over.");
                    }
                    py++;
                }

                char rckey = (char) rkey;
                //        left          right          up            down
                if (rckey == '%' || rckey == '\'' || rckey == '&' || rckey == '(')
                    cn.getTextWindow().output(px, py, 'P'); // VK kullanmadan test teknigi
                else cn.getTextWindow().output(rckey);


                keypr = 0;    // last action
            } // yön hareketleri


            for (int i = 0; i < robotCount; i++) {
                int XX = robots[i][1];
                int XY = robots[i][0];
                int randomdir = random.nextInt(4);
                if (randomdir == 0 && (map[XY + 1][XX] == ' ' || map[XY + 1][XX] == '©')) {
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0]);
                    cn.getTextWindow().output(' ');
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0] + 1);
                    cn.getTextWindow().output('X', Red);
                    map[XY + 1][XX] = 'X';
                    map[XY][XX] = ' ';
                    robots[i][0]++;


                } else if (randomdir == 1 && (map[XY - 1][XX] == ' ' || map[XY - 1][XX] == '©')) {
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0]);
                    cn.getTextWindow().output(' ');
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0] - 1);
                    cn.getTextWindow().output('X', Red);
                    map[XY - 1][XX] = 'X';
                    map[XY][XX] = ' ';
                    robots[i][0]--;


                } else if (randomdir == 2 && (map[XY][XX - 1] == ' ' || map[XY][XX - 1] == '©')) {
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0]);
                    cn.getTextWindow().output(' ');
                    cn.getTextWindow().setCursorPosition(robots[i][1] - 1, robots[i][0]);
                    cn.getTextWindow().output('X', Red);
                    map[XY][XX - 1] = 'X';
                    map[XY][XX] = ' ';
                    robots[i][1]--;


                } else if (randomdir == 3 && (map[XY][XX + 1] == ' ' || map[XY][XX + 1] == '©')) {
                    cn.getTextWindow().setCursorPosition(robots[i][1], robots[i][0]);
                    cn.getTextWindow().output(' ');
                    cn.getTextWindow().setCursorPosition(robots[i][1] + 1, robots[i][0]);
                    cn.getTextWindow().output('X', Red);
                    map[XY][XX + 1] = 'X';
                    map[XY][XX] = ' ';
                    robots[i][1]++;


                }
                if (map[py][px] == 'X') {

                    cn.getTextWindow().setCursorPosition(59, 27);
                    cn.getTextWindow().output("You hit the robot. Game over.");
                    gameisover = true;


                }


            } // robot hareketleri


            /////////////////////////////////////////////////////////////////////////////////


            if (map[py][px] == '1' || map[py][px] == '2' || map[py][px] == '3') {

                if (backpack.isFull()) {
                    backpack.pop();

                    char whatIsTheLastElement = (char) backpack.peek(); // backpack e eklemeden önce son elementi tutan değişken


                    if (whatIsTheLastElement == map[py][px]) {
                        player.getScore();
                        if (map[py][px] == '1') {
                            score += 10;

                        } else if (map[py][px] == '2') {
                            score += 40;

                        } else if (map[py][px] == '3') {
                            score += 90;
                            teleport++;

                        }
                        backpack.pop();
                        cn.getTextWindow().output(63, 12 - backpackCounter, ' ');
                        backpackCounter--;
                    } else {
                        backpack.push(map[py][px]);
                        backpackCounter++;
                    }
                } else {

                    /////// backpack ilk durum
                    if (backpack.isEmpty()) {  // bp boşsa
                        backpack.push(map[py][px]);
                        backpackCounter++;
                    } else if (!backpack.isEmpty() && !backpack.isFull()) { // boş değil ama dolu da değilse

                        char whatIsTheLastElement = (char) backpack.peek(); // backpack e eklemeden önce son elementi tutan değişken


                        if (whatIsTheLastElement == map[py][px]) {

                            if (map[py][px] == '1') {
                                score += 10;
                            } else if (map[py][px] == '2') {
                                score += 40;

                            } else if (map[py][px] == '3') {
                                score += 90;
                                teleport++;
                            }

                            backpack.pop();
                            cn.getTextWindow().output(63, 12 - backpackCounter, ' ');

                            backpackCounter--;
                        } else {
                            backpack.push(map[py][px]);
                            backpackCounter++;
                        }
                        player.getBackpack();
                    }
                }

                //displayWhatsInStack(backpack);
            }

            /////////////////////////////////////////////////

            map[py][px] = '©';
            cn.getTextWindow().output(px, py, '©', Yellow);

            if (time % 5 == 0) {

                inputQueueGenerator(inputQueue);
                generateElement();
                insertElement(inputQueue);
                printInputQueue(inputQueue);

            }

            cn.getTextWindow().output(px, py, '©', Yellow); // VK kullanmadan test teknigi
            cn.getTextWindow().setCursorPosition(60, 15);
            cn.getTextWindow().output("Time : " + time / 5);


            cn.getTextWindow().setCursorPosition(60, 19);
            cn.getTextWindow().output("Score : " + score);

            cn.getTextWindow().setCursorPosition(60, 17);
            cn.getTextWindow().output("Teleport : " + teleport);

            //backpack
            if (!backpack.isEmpty()) {
                cn.getTextWindow().output(63, 12 - backpackCounter, (char) backpack.peek());
            }

            checkBoulderFall();


            if (gameisover) {

                Ses.stop("oyun.wav");
                Ses.play("death.wav");
                for (int i = 0; i < 200; i++) // 10 kez renk değişimi yap
                {

                    cn.getTextWindow().output(px, py, '©', Red);
                    Thread.sleep(200); // 50ms beklet
                    cn.getTextWindow().output(px, py, '©', Yellow);
                    Thread.sleep(200); // 50ms beklet

                }
                    Ses.stop("death.wav");





            }


            Thread.sleep(200);
            time++;

        }

    }


/////////////////////////////////////////////////////// CONSTRUCTOR ÇIKIŞI

    public static CircularQueue inputQueueGenerator(CircularQueue inputQueue) {

        while (!inputQueue.isFull()) {
            char element = generateElement();
            inputQueue.enqueue(element);
        }
        return inputQueue;
    }

    public static Character generateElement() {
        Random rnd = new Random();
        int random = rnd.nextInt(40) + 1;


        if ((random >= 1) & (random <= 6))
            return '1';
        else if ((random >= 7) & (random <= 11))
            return '2';
        else if ((random >= 12) & (random <= 15))
            return '3';
        else if ((random >= 17) & (random <= 26))
            return 'O';
        else if ((random >= 27) & (random <= 35))
            return ':';
        else if ((random >= 36) & (random <= 40))
            return 'e';
        return 'X';
    }

    public static void insertElement(CircularQueue inputQueue) {
        //TODO basedSquare icin deger ataniyor mu ve find fonksiynolari dogru calisiyor mu?
        // TODO zamanlama bu sekilde mi kontrol edilcek?

        Character element = (Character) inputQueue.Peek();


        Random rnd = new Random();
        //eklemek için kullanılan
        int randomRow = rnd.nextInt(23) + 1;
        int randomColumn = rnd.nextInt(53) + 1;


        if (map[randomRow][randomColumn] != '#') {
            while (!(map[randomRow][randomColumn] == ':' || map[randomRow][randomColumn] == ' ')) {
                randomRow = rnd.nextInt(23) + 1;
                randomColumn = rnd.nextInt(53) + 1;
            }

            if (element == '1') {

                map[randomRow][randomColumn] = '1';
                cn.getTextWindow().output(randomColumn, randomRow, element, cyan);

            } else if (element == '2') {
                map[randomRow][randomColumn] = '2';
                cn.getTextWindow().output(randomColumn, randomRow, element, cyan);


            } else if (element == '3') {
                map[randomRow][randomColumn] = '3';
                cn.getTextWindow().output(randomColumn, randomRow, element, cyan);


            } else if (element == 'O') {

                // random boulder coordinates
                //kaya silmesi
                int row = rnd.nextInt(23) + 1;
                int column = rnd.nextInt(53) + 1;
                while (!(map[row][column] == 'O')) {
                    row = rnd.nextInt(23) + 1;
                    column = rnd.nextInt(53) + 1;
                }


                map[randomRow][randomColumn] = 'O';
                map[row][column] = ' ';

                cn.getTextWindow().output(randomColumn, randomRow, element, green);
                cn.getTextWindow().output(column, row, ' ');


            } else if (element == ':') {
                map[randomRow][randomColumn] = ':';
                cn.getTextWindow().output(randomColumn, randomRow, element);


            } else if (element == 'e') {
                map[randomRow][randomColumn] = ' ';
                cn.getTextWindow().output(randomColumn, randomRow, ' ');


            } else if (element == 'X') {
                map[randomRow][randomColumn] = 'X';
                cn.getTextWindow().output(randomColumn, randomRow, element, Red);
                if (element == 'X') {
                    robots[robotCount][0] = randomRow;
                    robots[robotCount][1] = randomColumn;
                    robotCount++;
                }

            }

        }


        inputQueue.dequeue();

    }

    public static void printInputQueue(CircularQueue inputQueue) {
        CircularQueue tempQueue = new CircularQueue(15);
        String str = "";

        while (!inputQueue.isEmpty()) {
            Object top = inputQueue.dequeue();
            tempQueue.enqueue(top);
            str += top;
        }
        while (!tempQueue.isEmpty()) {
            inputQueue.enqueue(tempQueue.dequeue());
        }

        cn.getTextWindow().setCursorPosition(60, 0);
        cn.getTextWindow().output("Input");
        cn.getTextWindow().setCursorPosition(60, 1);
        cn.getTextWindow().output("<<<<<<<<<<<<<<");
        cn.getTextWindow().setCursorPosition(60, 2);
        cn.getTextWindow().output(str);
        cn.getTextWindow().setCursorPosition(60, 3);
        cn.getTextWindow().output("<<<<<<<<<<<<<<");
    }

    static void displayWhatsInStack(Stack stack) {
        Stack temporary = new Stack(stack.size());

        while (!stack.isEmpty()) {
            temporary.push(stack.pop());
        }
        int e = 0;
        while (!temporary.isEmpty()) {
            char top = (char) temporary.peek();
            stack.push(temporary.pop());
            cn.getTextWindow().output(70 + e, 22, top);
            e++;
        }

        cn.getTextWindow().output(70 + e, 22, ' ');
        cn.getTextWindow().output(71 + e, 22, ' ');


    }  // to write it into console and changing values into letters


    static void checkBoulderFall() {
        Random rnd = new Random();
        for (int y = 23; y > 0; y--) {
            for (int x = 1; x < 54; x++) {
                if (map[y][x] == 'O') {
                    int newY = y;
                    int newX = x;
                    if (map[y + 1][x] == ' ') {
                        newY++;
                    } else if (map[y + 1][x] == 'O') {
                        if (map[y + 1][x + 1] == ' ' && map[y + 1][x - 1] == ' ') {
                            if (rnd.nextBoolean()) {
                                newY++;
                                newX--;
                            } else {
                                newY++;
                                newX++;
                            }
                        } else if (map[y + 1][x + 1] == ' ') {
                            newY++;
                            newX++;
                        } else if (map[y + 1][x - 1] == ' ') {
                            newY++;
                            newX--;
                        }
                    }
                    if (newX != x && map[y][newX] != ' ') {
                        newX = x;
                        newY = y;
                    }
                    map[y][x] = ' ';
                    map[newY][newX] = 'O';
                    cn.getTextWindow().output(x, y, ' ');
                    cn.getTextWindow().output(newX, newY, 'O', green);
                }
            }
        }
    }


}