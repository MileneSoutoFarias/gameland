package gameland1;


import java.util.Scanner;

public class Menu {
    
    public static int menu() {
        Scanner in = new Scanner(System.in);
        String texto = "\nMENU:"
                +"\n Ler de um ficheiro a informação dos jogos que compõem o evento... 1"
                +"\n Visualizar no ecrã toda a informação sobre os jogos que compõem o evento... 2"
                +"\n Ler de um ficheiro a informaçaon dos participantes de uma equipa ... 3"
                +"\n Visualizar toda a informaçao sobre os participantes ... 4"
                +"\n Alterar qualquer dado sobre um participante ... 5"
                +"\n ler de um ficheiro de texto toda a informaçao referente aos resultados de um jogo ... 6"
                +"\n Fazer backup dos fixeiros ...7"
                +"\n Calcular e guardar em memoria os premios dos participantes ...8"
                +"\n Remover toda a informaçao de uma equipa ...9"
                +"\n Visualizar os premios atribuidos a uma determinada equipa ...10"
                +"\n Visualizar a percentagem media obtida pelos jogadores, a percentagem de jogadores que nao participou ou desistiu e o valor total de premios obtidos para cada jogo ...11"
                +"\n Listar a informaçao de cada um dos participantes para o ecra ou para um fixeiro de texto ...12"
                + "\n FIM … 0"
                + "\nQual a sua opção?";
        System.out.printf("%n%s%n", texto);
        int op = in.nextInt();
        return op;
    }
    
}
