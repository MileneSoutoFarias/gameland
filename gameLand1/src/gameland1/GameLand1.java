package gameland1;

import static gameland1.Menu.menu;
import static gameland1.configuraçoes.*;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.Formatter;
import static java.lang.System.in;
import static java.lang.System.out;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Comparator;

public class GameLand1 {

    public final static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {

        String[][] info = new String[MAX_PARTICIPANTES][N_JOGOS];
        String[] jogos = new String[N_JOGOS];
        String[] jogosCarregados = new String[30];
        String[][] jogos2 = new String[N_JOGOS][3];
        String[][] participantes = new String[MAX_PARTICIPANTES][N_CAMPOS_INFO];
        int nParticipantes = 0;
        int numeroElementos = 0;
        int[][] pontosjogo = new int[MAX_PARTICIPANTES][N_JOGOS];
        double[][] prémios = new double[MAX_PARTICIPANTES][N_JOGOS];
        int op;
        do {
            op = menu();
            switch (op) {
                case 1:
                    System.out.println("Qual a data do evento (AAAAMMDD)");
                    String nomeFich = in.nextLine();
                    if (carregarJogosDoEvento(nomeFich + ".txt", jogos, jogos2)) {
                        System.out.println("Jogos carregados com sucesso");
                    } else {
                        System.out.println("Erro no carregamento dos jogos. Verifique ficheiro");
                    }
                    break;
                case 2:
                    visualizarInfoJogos(jogos);

                    break;
                case 3:
                    System.out.println("Introduza o nome da equipa ");
                    String nomeEquipa = in.nextLine();
                    nomeEquipa = (nomeEquipa + ".txt");
                    numeroElementos = lerInfoFicheiro(nomeEquipa, info, numeroElementos);
                    System.out.println("Informaçao correta");
                    break;
                case 4:
                    listagemPaginada(info, numeroElementos);
                    break;
                case 5:
                    System.out.println("Qual o email do participante ?");
                    String email = in.nextLine();
                    actualizarInfoParticipante(email, info, numeroElementos);

                    break;
                case 6:
                    System.out.println("Qual é a data do evento e o ID do jogo (AAAAMMDD_IDJogo) ?");
                    String fich1 = in.nextLine();
                    if (lerCarregarInfoResultados(fich1, participantes, jogos2, pontosjogo, jogosCarregados)) {
                        for (int i = 0; i < pontosjogo.length; i++) {
                            String linha = Arrays.toString(pontosjogo[i]);
                            System.out.println(linha);
                        }

                        System.out.println("Pontuação devidamente carregada!");
                    } else {
                        System.out.println("Erro ao carregar a pontuação!");
                    }

                    break;
                case 7:
                    backupFile(participantes, numeroElementos, pontosjogo);

                    break;
                case 8:
                    if(calcularPremios(participantes, jogos2, prémios, pontosjogo)){
                        System.out.println("premios calculados com sucesso");
                    }else{
                        System.out.println("erro ao carregar premios");
                    }

                    break;
                case 9: System.out.println("Introduza o nome da equipa: ");
                    String equipaAEliminar = in.nextLine();
                    participantes = eliminarEquipa(participantes, equipaAEliminar);
                    numeroElementos = registosAtivos(participantes);
                    try {
                        pontosjogo = new int[MAX_PARTICIPANTES][N_JOGOS];
                        prémios = new double[MAX_PARTICIPANTES][N_JOGOS];
                        for (int i = 0; i < registosAtivos(jogosCarregados); i++) {
                            lerCarregarInfoResultados(jogosCarregados[i], participantes, jogos2, pontosjogo, jogosCarregados);
                        }
                    } catch (Exception e) {
                    }
                       
                    break;

                   
                case 10:
                    System.out.println("Introduza o nome da equipa: ");
                    String equipa = in.nextLine();
                    vizualizarPremioPorEquipa(equipa, participantes, prémios);

                    break;
                case 11:
                    System.out.println("Qual o jogo que pertende vizualizar ?");
                    String opcaoJogo = in.nextLine();

                    break;
                case 12:
                    opcao12(participantes, prémios);

                    break;
                case 0:
                    System.out.println(
                            "Já fez todas as gravações necessárias? Confirma terminar(s/n)?");
                    char resp = (in.next()).charAt(0);
                    if (resp != 's' && resp != 'S') {
                        op = 1;
                    }
                    break;
                default:
                    System.out.println("Opção incorreta. Repita");
                    break;
            }
        } while (op != 0);
    }

    private static boolean carregarJogosDoEvento(String nomeFichJogos, String[] jogos, String[][] jogos2) throws FileNotFoundException {
        File f = new File(nomeFichJogos);
        if (!f.exists()) {
            System.out.println("FIcheiro inexistente");
            return false;
        }
        Scanner fInput = new Scanner(new File(nomeFichJogos));
        int i = 0;
        while (fInput.hasNextLine() && i < N_JOGOS) {
            String linha = fInput.nextLine();
            if ((linha.trim()).length() > 0) {
                String[] temp = linha.split("-");
                jogos2[i][0] = temp[0];
                jogos2[i][1] = temp[1];
                jogos2[i][2] = temp[2];
                jogos[i] = linha;
                i++;
            }
        }
        fInput.close();
        if (i == N_JOGOS) {
            return true;
        }
        return false;
    }

    private static void visualizarInfoJogos(String[] jogos) {
        System.out.println("Jogos do evento");
        System.out.printf("%15s%15s%15s%n", "ID do jogo", "Tipo de jogo", "Max. de pontos");
        for (int i = 0; i < jogos.length; i++) {
            String[] temp = jogos[i].split("-");
            System.out.printf("%15s%15s%15s%n", temp[0], temp[1], temp[2]);
        }
    }

    public static int lerInfoFicheiro(String nomeFich, String[][] info, int numeroElementos) throws FileNotFoundException {
        File f = new File(nomeFich);
        if (!f.exists()) {
            System.out.println("FIcheiro inexistente");
            return 0;
        }
        Scanner fInput = new Scanner(new File(nomeFich));
        int nElemsInic = numeroElementos;
        while (fInput.hasNext() && numeroElementos < MAX_PARTICIPANTES) {
            String linha = fInput.nextLine();
            if (linha.trim().length() > 0) {
                numeroElementos = guardarDados(linha, info, numeroElementos);
            }
        }
        fInput.close();
        if (numeroElementos - nElemsInic != 3) {
            numeroElementos = nElemsInic;
        }
        return numeroElementos;
    }

    private static int guardarDados(String linha, String[][] info, int numeroElementos) {
        String[] temp = linha.split(SEPARADOR_DADOS_FICH);
        if (temp.length == N_CAMPOS_INFO) {
            String num = temp[0].trim();
            int pos = pesquisarElemento(num, numeroElementos, info);
        }
        for (int i = 0; i < N_CAMPOS_INFO; i++) {
            info[numeroElementos][i] = temp[i].trim();
        }
        numeroElementos++;

        return numeroElementos;
    }

    public static int pesquisarElemento(String valor, int numeroElementos, String[][] jogos2) {
        for (int i = 0; i < numeroElementos; i++) {
            if (jogos2[i][0] != null && jogos2[i][0].equals(valor)) {
                return i;
            }
        }
        return -1;
    }

    public static int pesquisarElemento(int valor, int numeroElementos, String[][] jogos2) {
        for (int i = 0; i < numeroElementos; i++) {
            if (jogos2[i][0].equals(valor)) {
                return i;
            }
        }
        return -1;
    }
       public static int pesquisarElemento(String valor, int numeroElementos, String[] mat) {
        for (int i = 0; i < numeroElementos; i++) {
            if (mat[i] != null && mat[i].equals(valor)) {
                return i;
            }
        }
        return -1;
    }

    private static void listagemPaginada(String[][] matriz, int numeroElementos) {
        int contPaginas = 0;
        for (int i = 0; i < numeroElementos; i++) {
            if (i % MAX_LINHAS_PAGINA == 0) {
                if (contPaginas > 0) {
                    pausa();
                }
                contPaginas++;
                System.out.println("\nPÁGINA: " + contPaginas);
                cabecalho();
            }
            for (int j = 0; j < N_CAMPOS_INFO; j++) {
                if (j == 1) {
                    System.out.printf("%30s", matriz[i][j]);
                } else {
                    System.out.printf("%10s", matriz[i][j]);
                }
            }
            System.out.println("");
        }
        pausa();
    }

    private static void cabecalho() {
        System.out.printf("%50s%n", "PARTICIPANTES");
        System.out.printf("%75s%n", "==================================================");
    }

    private static void pausa() {
        System.out.println("\n\nPara continuar digite ENTER\n");
        in.nextLine();
    }

    public static boolean actualizarInfoParticipante(String eMail, String[][] matriz, int numeroElementos) {
        int pos;
        if ((pos = pesquisarElemento(eMail, numeroElementos, matriz)) > -1) {
            int op;
            do {
                Formatter out = new Formatter(System.out);
                mostrarParticipante(out, matriz[pos]);
                op = menuInfoParticipante();
                switch (op) {
                    case 1:
                        System.out.println("Novo EMai:");
                        matriz[pos][0] = in.nextLine();
                        break;
                    case 2:
                        System.out.println("Novo nome:");
                        matriz[pos][1] = in.nextLine();
                        break;
                    case 3:
                        System.out.println("Nova data nascimento:");
                        matriz[pos][2] = in.nextLine();
                        break;
                    case 0:
                        System.out.println("FIM");
                        break;
                    default:
                        System.out.println("Opção incorreta");
                        break;
                }
            } while (op != 0);
            return true;
        }
        System.out.printf("O participante %s não foi encontrado!", eMail);
        return false;
    }

    private static void mostrarParticipante(Formatter out, String[] participante) {
        for (int j = 0; j < N_CAMPOS_INFO; j++) {
            if (j == 1) {
                out.format("%30s;", participante[j]);
            } else {
                out.format("%12s;", participante[j]);
            }
        }
    }

    private static int menuInfoParticipante() {
        String texto = "ATUALIZAR INFORMAÇÃO DE PARTICIPANTE"
                + "\n EMail ... 1"
                + "\n NOME ... 2"
                + "\n DATA NASCIMENTO ... 3"
                + "\n TERMINAR ... 0"
                + "\n\nQUAL A SUA OPÇÃO?";
        System.out.printf("%n%s%n", texto);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }

    public static Boolean lerCarregarInfoResultados(String nomeFicheiro, String[][] participantes, String[][] jogos2, int[][] pontosJogo, String[] jogosCarregados) throws FileNotFoundException {
        try {
            Scanner fInput = new Scanner(new File(nomeFicheiro + ".txt"));
            String[] nomefichdiv = nomeFicheiro.split("_");
            if (pesquisarElemento(nomeFicheiro, 30, jogosCarregados) >= 0) {
                jogosCarregados[registosAtivos(jogosCarregados)] = nomeFicheiro;
            }
            int IdJogo = Integer.parseInt(nomefichdiv[1]);
            while (fInput.hasNext()) {
                String linha = fInput.nextLine();
                
                if (linha.trim().length() > 0 && linha.split(SEPARADOR_DADOS_FICH).length == 2) {
                    
                    String[] jogador = linha.split(SEPARADOR_DADOS_FICH);
                  
                    int idxPart = pesquisarElemento(jogador[0], MAX_PARTICIPANTES, participantes);
                    if (idxPart != -1) {
                        
                        for (int i = 0; i < jogos2.length; i++) {
                            int TempID = Integer.parseInt(jogos2[i][0]);
                            if (IdJogo == TempID) {
                                pontosJogo[idxPart][i] = Integer.parseInt(jogador[1]);
                            }
                        }
                    }
                }
            }
            fInput.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("Ficheiro não existe");
            return false;
        }
    }

    public static void backupFile(String[][] participantes, int numeroElementos, int[][] pontosjogo) throws FileNotFoundException {
        try {
            if (participantes.length == 0) {
                System.out.println("Não existe informação em memória");
                return;
            }
            Formatter out = new Formatter(new File(BACKUP_FILE));
            String linha = new String();
            for (int i = 0; i < numeroElementos; i++) {

                for (int j = 0; j < N_CAMPOS_INFO; j++) {
                    linha += participantes[i][j] + " | ";
                }
                for (int k = 0; k < N_JOGOS; k++) {
                    linha += pontosjogo[i][k] + " | ";
                }
                out.format(linha);
                out.format("\n");
                linha = "";
            }
            out.close();
            System.out.println("Backup guardado com sucesso");
        } catch (FileNotFoundException e) {
            System.out.println("Surgiu um erro ao efectuar Backup!");
        }
    }

    private static boolean calcularPremios(String[][] participantes, String[][] jogos2, double[][] premios, int[][] pontosJogo) {

        carregarPremioDoVencedor(participantes, jogos2, premios, pontosJogo);
        carregarPremioDaEquipaVencedora(participantes, jogos2, premios, pontosJogo);
        carregarPremioDeAniversario(participantes, premios);
        return true;
     
    }

    private static void carregarPremioDoVencedor(String[][] participantes, String[][] jogos2, double[][] premios, int[][] pontosjogo) {
        int[][] partPontos = obterParticipantesComPontos(participantes, jogos2, pontosjogo);

        int addLine = 0;
        int[] vencedores = new int[0];
        int valorMaximo = 0;

        for (int jogoIdx = 0; jogoIdx < jogos2.length; jogoIdx++) {
            valorMaximo = 0;
            int sumaPontosjogo = 0;
            for (int i = 0; i < partPontos.length; i++) {

                if (partPontos[i][0] >= 0) {
                    int participanteIdx = partPontos[i][0];
                    int jogoPontos = partPontos[i][jogoIdx + 1];
                    sumaPontosjogo += jogoPontos;

                    if (jogoPontos > 0) {
                        if (jogoPontos > valorMaximo) {

                            valorMaximo = jogoPontos;
                            vencedores = new int[1];
                            vencedores[0] = participanteIdx;
                        } else if (jogoPontos == valorMaximo) {

                            addLine++;
                            vencedores = Arrays.copyOf(vencedores, addLine + 1);
                            vencedores[addLine - 1] = participanteIdx;
                        }
                    }
                }
            }
            if (sumaPontosjogo == 0) {
                vencedores = new int[0];
            } else {
//				
                if (vencedores.length > 0) {
                    for (int i = 0; i < vencedores.length; i++) {
                        premios[vencedores[i]][jogoIdx] = valorMaximo / Double.parseDouble(jogos2[jogoIdx][2]) * 100;
                    }
                }

            }
        }

    }

    private static void carregarPremioDaEquipaVencedora(String[][] participantes, String[][] jogos2, double[][] premios, int[][] pontosjogo) {
        int[][] partPontos = obterParticipantesComPontos(participantes, jogos2, pontosjogo);
        int numPartAtivos = registosAtivos(participantes);
        String[][] Equipas = new String[numPartAtivos * jogos2.length][4];
        int EquipasLn = 0;
        String nomeEquipa = "";
        String equipaAtual = "";
        for (int jogoIdx = 0; jogoIdx < jogos2.length; jogoIdx++) {
            int sumaPontosjogo = 0;
            String temp_participantes = "";
            for (int partIdx = 0; partIdx < partPontos.length; partIdx++) {
                if (partPontos[partIdx][0] >= 0) {
                    equipaAtual = participantes[partIdx][3].trim();
                    int result = equipaAtual.compareTo(nomeEquipa);
                    if (result == 0) {
                        temp_participantes += Integer.toString(partIdx) + SEPARADOR_DADOS_FICH;
                        sumaPontosjogo += partPontos[partIdx][jogoIdx + 1];
                    } else {
                        nomeEquipa = participantes[partIdx][3].trim();
                        temp_participantes = Integer.toString(partIdx) + SEPARADOR_DADOS_FICH;
                        sumaPontosjogo = partPontos[partIdx][jogoIdx + 1];
                    }
                    if (sumaPontosjogo > 0 && temp_participantes.split(SEPARADOR_DADOS_FICH).length == 3) {
                        Equipas[EquipasLn][0] = Integer.toString(jogoIdx);
                        Equipas[EquipasLn][1] = nomeEquipa;
                        Equipas[EquipasLn][2] = temp_participantes;
                        Equipas[EquipasLn][3] = Integer.toString(sumaPontosjogo);
                        EquipasLn++;
                    }
                }
            }
        }
        for (int jogoIdx = 0; jogoIdx < jogos2.length; jogoIdx++) {

            int pontosMaximos = 0;
            String equipaVencedora = "";
            String Vencedores = "";

            for (int linhaEquipas = 0; linhaEquipas < registosAtivos(Equipas); linhaEquipas++) {
                int jogoEquipa = Integer.parseInt(Equipas[linhaEquipas][0]);

                if (jogoEquipa == jogoIdx) {

                    int jogoPontos = Integer.parseInt(Equipas[linhaEquipas][3]);

                    if (jogoPontos > 0) {
                        if (jogoPontos > pontosMaximos) {

                            pontosMaximos = jogoPontos;
                            equipaVencedora = Equipas[linhaEquipas][1];
                            Vencedores = Equipas[linhaEquipas][2];
                        }
                    }
                }
            }

            if (equipaVencedora.length() > 0) {

                String[] vencedores = Vencedores.split(SEPARADOR_DADOS_FICH);

                for (int i = 0; i < vencedores.length; i++) {
                    int linha = Integer.parseInt(vencedores[i]);
                    String data = participantes[linha][2];
                    premios[linha][jogoIdx] += 50;
                }
            }

        }

    }

    private static int[][] obterParticipantesComPontos(String[][] participantes, String[][] jogos2, int[][] pontosjogo) {

        int[][] partPontos = new int[participantes.length][jogos2.length + 1];

        for (int j = 0; j < jogos2.length; j++) {
            for (int p = 0; p < participantes.length; p++) {
                if (participantes[p][0] != null) {
                    partPontos[p][0] = p;
                    partPontos[p][j + 1] = pontosjogo[p][j];
                } else {
                    partPontos[p][0] = -1;
                }
            }
        }
        return partPontos;
    }

    public static int registosAtivos(String[][] matriz) {
        int i = 0;
        for (int j = 0; j < matriz.length; j++) {
            i += (matriz[j][0] != null) ? 1 : 0;
        }
        return i;
    }
     public static int registosAtivos(String[] matriz) {
        int i = 0;
        for (int j = 0; j < matriz.length; j++) {
            i += (matriz[j] != null) ? 1 : 0;
        }
        return i;
    }

    private static void carregarPremioDeAniversario(String[][] participantes, double[][] premios) {
        for (int i = 0; i < participantes.length; i++) {
            String data = participantes[i][2];
            double premio = premioAniversario(data);
            for (int col = 0; col < premios[i].length; col++) {
                if (premios[i][col] > 0) {
                    premios[i][col] += premio;
                }
            }

        }
    }

    public static double premioAniversario(String data) {
        try {
            int idade = idade(converterddmmaaaaParaaaammdd(data));
            LocalDate dateOfBirth = converterddmmaaaaParaLocalDate(data);
            MonthDay birthday = MonthDay.of(dateOfBirth.getMonth(), dateOfBirth.getDayOfMonth());
            MonthDay currentMonthDay = MonthDay.from(LocalDate.now());
            return (currentMonthDay.equals(birthday)) ? 2 * idade : 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static String converterddmmaaaaParaaaammdd(String data) {
        String[] aux = data.trim().split("/");
        String dia = aux[0].length() < 2 ? "0" + aux[0] : aux[0];
        String mes = aux[1].length() < 2 ? "0" + aux[1] : aux[1];
        return aux[2] + mes + dia;
    }

    public static LocalDate converterddmmaaaaParaLocalDate(String data) {
        String[] aux = data.trim().split("/");
        String dia = aux[0].length() < 2 ? "0" + aux[0] : aux[0];
        String mes = aux[1].length() < 2 ? "0" + aux[1] : aux[1];
        try {
            return LocalDate.parse(aux[2] + "-" + mes + "-" + dia);
        } catch (DateTimeParseException e) {
            System.out.println("a data " + data + " está errada!");
            return null;
        }
    }

    public static int idade(String anoMesDia) {
        int ano = Integer.parseInt(anoMesDia.substring(0, 4));
        int mes = Integer.parseInt(anoMesDia.substring(4, 6));
        int dia = Integer.parseInt(anoMesDia.substring(6, 8));
        Calendar hoje = Calendar.getInstance();
        int diaH = hoje.get(Calendar.DAY_OF_MONTH);
        int mesH = hoje.get(Calendar.MONTH) + 1;
        int anoH = hoje.get(Calendar.YEAR);
        if (mesH > mes || mesH == mes && diaH >= dia) {
            return anoH - ano;
        }
        return anoH - ano - 1;
    }
    
     public static String[][] eliminarEquipa(String[][] participantes, String equipaAEliminar) {

        String[][] temp = new String[MAX_PARTICIPANTES][N_CAMPOS_INFO];
        int j = 0;
        for (int i = 0; i < registosAtivos(participantes); i++) {
            if (participantes[i][3].compareTo(equipaAEliminar) != 0) {
                temp[j][0] = participantes[i][0];
                temp[j][1] = participantes[i][1];
                temp[j][2] = participantes[i][2];
                temp[j][3] = participantes[i][3];
                j++;
            }
        }
        return temp;

    }
     private static void vizualizarPremioPorEquipa(String equipa, String[][] participantes, double[][] premios) {

        String[] nomes = new String[0];
        String[][] pOrdenados = new String[3][2];
        for (int idx = 0; idx < registosAtivos(participantes); idx++) {
            if (participantes[idx][3].equals(equipa)) {
                nomes = Arrays.copyOf(nomes, nomes.length + 1);
                nomes[nomes.length - 1] = participantes[idx][1];

            }
        }
        ordenarAlf(nomes, nomes.length);
        for (int i = 0; i < nomes.length; i++) {
            for (int idx = 0; idx < registosAtivos(participantes); idx++) {
                if (participantes[idx][1] == nomes[i]) {
                    pOrdenados[i][0] = nomes[i];
                    pOrdenados[i][1] = Double.toString(totalPremiosPart(premios, idx));
                }
            }
        }
     
        Formatter str = new Formatter();
        str.format("%20s %24s\n", "Nomes", "Premios");
        for (int i = 0; i < pOrdenados.length; i++) {
            double valor = Double.parseDouble(pOrdenados[i][1]);
            str.format("%23s %20.2f€\n", pOrdenados[i][0], valor);
        }
        System.out.println(str.toString());
    }
      public static void ordenarAlf(String[] nome, int n) {
        String temp;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nome[i].compareTo(nome[j]) > 0) {
                    temp = nome[i]; nome[i] = nome[j];nome[j] = temp;
                }
            }
        }
    }
      public static double totalPremiosPart(double[][] premios, int idx) {
        double soma = 0;
        for (int j = 0; j < N_JOGOS; j++) {
            soma = soma + premios[idx][j];
        }
        return (soma);
    }

    public static void VizualizarParaCadaJogo(String opcaoJogo, int[][] pontosJogo, double[][] premios, String[][] jogos2, String[][] participantes) {

        int op;
        do {
            op = menuVizualizarParaUmJogo(opcaoJogo);
            switch (op) {
                case 1:
                    pontuacaoMedia(pontosJogo, opcaoJogo);
                    break;
                case 2:
                    percentagemJogadoresDesistentes(pontosJogo, opcaoJogo);
                    break;
                case 3:
                    totalPremios(participantes, jogos2, premios, opcaoJogo);
                    break;
                case 0:
                    System.out.println("FIM");
                    break;
                default:
                    System.out.println("Opção incorreta");
                    op = 1;
                    break;
            }
        } while (op != 0);
    }

    private static int menuVizualizarParaUmJogo(String opcaoJogo) {
        String texto = "VIZUALIZAR PARA O JOGO " + opcaoJogo
                + "\nMÉDIA DE PONTOS ... 1"
                + "\nPERCENTAGEM DE JODADORES QUE NAO PARTICIPARAM OU DESISTIRAM ... 2"
                + "\nVALOR TOTAL DE PRÉMIOS ATRIBUIDOS NESSE JOGO... 3"
                + "\n0TERMINAR ... 0"
                + "\n\nINTRODUZA OPÇÃO ?";
        System.out.printf("%n%s%n", texto);
        try {
            int op = in.nextInt();
            in.nextLine();
            return op;
        } catch (Exception e) {
            in.nextLine();
            return -1;
        }
    }

    private static double pontuacaoMedia(int[][] pontosJogo, String opcaoJogo) {

        int soma = 0;
        double media = 0;
        int coluna = 0;
        coluna = Integer.parseInt(opcaoJogo) - 1;
        for (int i = 0; i < pontosJogo.length; i++) {
            soma = soma + pontosJogo[i][coluna];
            media = soma / MAX_PARTICIPANTES;
        }
        System.out.println("A pontuação média no jogo " + opcaoJogo + " é: " + media);
        return 0;
    }
        
        private static int percentagemJogadoresDesistentes(int[][] pontosJogo, String opcaoJogo) {

        double Desistentes = 0;
        double percent = 0;
        int coluna = 0;
        coluna = Integer.parseInt(opcaoJogo) - 1;
        for (int i = 0; i < pontosJogo.length; i++) {
            if (pontosJogo[i][coluna] == 0) {
                Desistentes++;
            }
        }
        percent = (Desistentes * 100) / MAX_PARTICIPANTES;
        System.out.println(percent + "% dos jogadores que se inscreveram, desistiu.");
        return 0;
    }
        private static void totalPremios(String[][] participantes, String[][] jogos2, double[][] premios, String opcaoJogo) {

        int totalPremios = 0;
        int coluna = 0;
        coluna = Integer.parseInt(opcaoJogo) - 1;

        for (int idx = 0; idx < registosAtivos(participantes); idx++) {
            int prt = (int) Math.round(premios[idx][coluna]);
            totalPremios = totalPremios + prt;
        }
        System.out.println("O total de Prémios atribuidos no jogo " + opcaoJogo + " é: " + totalPremios);
       
    }
    private static void opcao12(String[][] participantes, double[][] premios) {
        int op;
        do {
            op = menuListaDePremios();
            switch (op) {
                case 1:
                    
                    opcao12ListarParaEcran(participantes, premios);
                    break;
                case 2:
                   
                    opcao12ListarParaFicheiro(participantes, premios);
                    
                    break;
                case 0: 
                    break;
                default:
                    System.out.println("Opção incorreta");
                    op = 1;
                    break;
            }
        } while (op != 0);
    }
     public static int menuListaDePremios() {

		String texto = "Opção selecionada 12: (Lista de informação relativa a cada um dos participantes)\n"
				+ "\nIndique uma das seguintes opções: \n"
				+ "\n 1  - Pretende visualizar os resultados?"
				+ "\n 2  - Gravar dados em ficheiro!"
				+ "\n 0  - Voltar a menu anterior";

		System.out.printf("%n%s%n", texto);

		try {
			int op = in.nextInt();
			in.nextLine();
			return op;
		} catch (Exception e) {
			in.nextLine();
			return 99;
		}
	}


    private static void opcao12ListarParaEcran(String[][] participantes, double[][] premios) {
        String[][] lista = new String[0][0];
        lista = opcao12ObterLista(participantes, premios);
        String strLista = opcao12ObterResultado(lista);
        System.out.println(strLista);
    }

    private static void opcao12ListarParaFicheiro(String[][] participantes, double[][] premios) {
        String[][] lista = new String[0][0];
        lista = opcao12ObterLista(participantes, premios);
        String strLista = opcao12ObterResultado(lista);
        try {
            Formatter out = new Formatter(new File("Premios.txt"));
            out.format(strLista);
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao gravar ficheiro!");
        }
    }

    private static String[][] opcao12ObterLista(String[][] participantes, double[][] premios) {
        String[][] lista = new String[MAX_PARTICIPANTES][4];
        for (int idx = 0; idx < registosAtivos(participantes); idx++) {
            String sEmail = participantes[idx][0];
            String sNome = apelidoELetraInicial(participantes[idx][1]);
            String eEquipa = participantes[idx][3];
            double dPremio = 0;
            for (int col = 0; col < premios[idx].length; col++) {
                dPremio += premios[idx][col];
            }
            String sPremio = Double.toString(dPremio);
            lista[idx][0] = sEmail;
            lista[idx][1] = sNome;
            lista[idx][2] = eEquipa;
            lista[idx][3] = sPremio;
        }

        int numLinhas = registosAtivos(lista);
        String[][] preResultado = new String[numLinhas][4];
        String[][] resultado = new String[numLinhas][4];
        Double[][] ordenar = new Double[numLinhas][2];
        for (int idx = 0; idx < registosAtivos(lista); idx++) {
            preResultado[idx][0] = lista[idx][0];
            preResultado[idx][1] = lista[idx][1];
            preResultado[idx][2] = lista[idx][2];
            preResultado[idx][3] = lista[idx][3];
            ordenar[idx][1] = Double.valueOf(idx);
            ordenar[idx][0] = Double.parseDouble(lista[idx][3]);
        }

        Arrays.sort(ordenar, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] s1, Double[] s2) {
                if (s1[0] > s2[0]) {
                    return -1;
                } else if (s1[0] < s2[0]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < ordenar.length; i++) {
            int linha = (int) Math.round(ordenar[i][1]);
            resultado[i][0] = preResultado[linha][0];
            resultado[i][1] = preResultado[linha][1];
            resultado[i][2] = preResultado[linha][2];
            resultado[i][3] = preResultado[linha][3];
        }

        return resultado;
    }
     public static String apelidoELetraInicial(String str) {
        str = str.trim();
        int last = str.lastIndexOf(' '); 
        String resultado = str.substring(last + 1) + " " + str.substring(0, 1) + ".";
        return resultado;
    }

    private static String opcao12ObterResultado(String[][] lista) {
        Formatter str = new Formatter();
        str.format("%40s%s\n\n", "LISTAGEM DE PRÉMIOS", "");
        str.format("%15s%15s%15s%20s\n", "Email", "Nome", "Equipa",
                "Total de Prémios");
        for (int i = 0; i < lista.length; i++) {
            str.format("%15s%15s%15s%20s\n", lista[i][0], lista[i][1],
                    lista[i][2], lista[i][3]);
        }
        return str.toString();
    }

}


   
    
   


