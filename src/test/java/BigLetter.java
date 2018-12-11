import java.util.ArrayList;
import java.util.List;

public class BigLetter {

    private static String[][] letters;

    static {
        letters = new String[31][6];

        //A
        letters[0][0] = "a";
        letters[0][1] = "███";
        letters[0][2] = "█░█";
        letters[0][3] = "███";
        letters[0][4] = "█░█";
        letters[0][5] = "█░█";

        //B
        letters[1][0] = "b";
        letters[1][1] = "██░";
        letters[1][2] = "█░█";
        letters[1][3] = "██░";
        letters[1][4] = "█░█";
        letters[1][5] = "██░";

        //C
        letters[2][0] = "c";
        letters[2][1] = "███";
        letters[2][2] = "█░░";
        letters[2][3] = "█░░";
        letters[2][4] = "█░░";
        letters[2][5] = "███";

        //D
        letters[3][0] = "d";
        letters[3][1] = "██░";
        letters[3][2] = "█░█";
        letters[3][3] = "█░█";
        letters[3][4] = "█░█";
        letters[3][5] = "██░";

        //E
        letters[4][0] = "e";
        letters[4][1] = "███";
        letters[4][2] = "█░░";
        letters[4][3] = "██░";
        letters[4][4] = "█░░";
        letters[4][5] = "███";

        //F
        letters[5][0] = "f";
        letters[5][1] = "███";
        letters[5][2] = "█░░";
        letters[5][3] = "██░";
        letters[5][4] = "█░░";
        letters[5][5] = "█░░";

        //G
        letters[6][0] = "g";
        letters[6][1] = "███";
        letters[6][2] = "█░░";
        letters[6][3] = "███";
        letters[6][4] = "█░█";
        letters[6][5] = "███";

        //H
        letters[7][0] = "h";
        letters[7][1] = "█░█";
        letters[7][2] = "█░█";
        letters[7][3] = "███";
        letters[7][4] = "█░█";
        letters[7][5] = "█░█";

        //I
        letters[8][0] = "i";
        letters[8][1] = "░█░";
        letters[8][2] = "░░░";
        letters[8][3] = "░█░";
        letters[8][4] = "░█░";
        letters[8][5] = "░█░";

        //J
        letters[9][0] = "j";
        letters[9][1] = "░██";
        letters[9][2] = "░░█";
        letters[9][3] = "░░█";
        letters[9][4] = "░░█";
        letters[9][5] = "███";

        //K
        letters[10][0] = "k";
        letters[10][1] = "█░█";
        letters[10][2] = "██░";
        letters[10][3] = "█░░";
        letters[10][4] = "██░";
        letters[10][5] = "█░█";

        //L
        letters[11][0] = "l";
        letters[11][1] = "█░░";
        letters[11][2] = "█░░";
        letters[11][3] = "█░░";
        letters[11][4] = "█░░";
        letters[11][5] = "███";

        //M
        letters[12][0] = "m";
        letters[12][1] = "█░█";
        letters[12][2] = "███";
        letters[12][3] = "█░█";
        letters[12][4] = "█░█";
        letters[12][5] = "█░█";

        //N
        letters[13][0] = "n";
        letters[13][1] = "███";
        letters[13][2] = "█░█";
        letters[13][3] = "█░█";
        letters[13][4] = "█░█";
        letters[13][5] = "█░█";

        //O
        letters[14][0] = "o";
        letters[14][1] = "███";
        letters[14][2] = "█░█";
        letters[14][3] = "█░█";
        letters[14][4] = "█░█";
        letters[14][5] = "███";

        //P
        letters[15][0] = "p";
        letters[15][1] = "██░";
        letters[15][2] = "█░█";
        letters[15][3] = "██░";
        letters[15][4] = "█░░";
        letters[15][5] = "█░░";

        //Q
        letters[16][0] = "q";
        letters[16][1] = "██░";
        letters[16][2] = "██░";
        letters[16][3] = "██░";
        letters[16][4] = "██░";
        letters[16][5] = "███";

        //R
        letters[17][0] = "r";
        letters[17][1] = "██░";
        letters[17][2] = "█░█";
        letters[17][3] = "██░";
        letters[17][4] = "█░█";
        letters[17][5] = "█░█";

        //S
        letters[18][0] = "s";
        letters[18][1] = "░██";
        letters[18][2] = "█░░";
        letters[18][3] = "██░";
        letters[18][4] = "░░█";
        letters[18][5] = "███";

        //T
        letters[19][0] = "t";
        letters[19][1] = "███";
        letters[19][2] = "░█░";
        letters[19][3] = "░█░";
        letters[19][4] = "░█░";
        letters[19][5] = "░█░";

        //U
        letters[20][0] = "u";
        letters[20][1] = "█░█";
        letters[20][2] = "█░█";
        letters[20][3] = "█░█";
        letters[20][4] = "█░█";
        letters[20][5] = "███";

        //V
        letters[21][0] = "v";
        letters[21][1] = "█░█";
        letters[21][2] = "█░█";
        letters[21][3] = "█░█";
        letters[21][4] = "█░█";
        letters[21][5] = "██░";

        //W
        letters[22][0] = "w";
        letters[22][1] = "█░█";
        letters[22][2] = "█░█";
        letters[22][3] = "█░█";
        letters[22][4] = "███";
        letters[22][5] = "█░█";

        //X
        letters[23][0] = "posX";
        letters[23][1] = "█░█";
        letters[23][2] = "█░█";
        letters[23][3] = "░█░";
        letters[23][4] = "█░█";
        letters[23][5] = "█░█";

        //Y
        letters[24][0] = "y";
        letters[24][1] = "█░█";
        letters[24][2] = "█░█";
        letters[24][3] = "░█░";
        letters[24][4] = "░█░";
        letters[24][5] = "░█░";

        //flag
        letters[25][0] = "posZ";
        letters[25][1] = "███";
        letters[25][2] = "░░█";
        letters[25][3] = "░█░";
        letters[25][4] = "█░░";
        letters[25][5] = "███";

        //
        letters[26][0] = " ";
        letters[26][1] = "░░░";
        letters[26][2] = "░░░";
        letters[26][3] = "░░░";
        letters[26][4] = "░░░";
        letters[26][5] = "░░░";

        //:
        letters[27][0] = ":";
        letters[27][1] = "░░░";
        letters[27][2] = "░█░";
        letters[27][3] = "░░░";
        letters[27][4] = "░█░";
        letters[27][5] = "░░░";

        //(
        letters[28][0] = "(";
        letters[28][1] = "░██";
        letters[28][2] = "█░░";
        letters[28][3] = "█░░";
        letters[28][4] = "█░░";
        letters[28][5] = "░██";

        //>
        letters[29][0] = ">";
        letters[29][1] = "░░░";
        letters[29][2] = "██░";
        letters[29][3] = "░░█";
        letters[29][4] = "██░";
        letters[29][5] = "░░░";

        //1
        letters[30][0] = "1";
        letters[30][1] = "░█░";
        letters[30][2] = "██░";
        letters[30][3] = "░█░";
        letters[30][4] = "░█░";
        letters[30][5] = "███";
    }

    private static int findChar(char c) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i][0].charAt(0) == c) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
            String toWrite = "HI RAKIN".toLowerCase();

        List<Integer> sequence = new ArrayList();

        for (int i = 0; i < toWrite.length(); i++) {
            sequence.add(findChar(toWrite.charAt(i)));
        }

        for (int t = 0; t < sequence.size(); t += 8) {

            StringBuilder builder = new StringBuilder();
            int length = 0;

            int max = Math.min(t + 8, sequence.size());

            for (int i = 1; i < 6; i++) {
                for (int i1 = t; i1 < max; i1++) {
                    int c = sequence.get(i1);

                    if (i == 1) {
                        length += 4;
                    }

                    builder.append(letters[c][i]).append('░');

                    if (i1 + 1 == max) {
                        for (int d = length; d < 20; d++) builder.append('░');

                    }
                }
                if(i != 5)
                builder.append('\n');
            }

            System.out.println(builder);
            System.out.println("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░");
        }

    }
}
