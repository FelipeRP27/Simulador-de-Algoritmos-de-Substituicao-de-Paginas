import java.util.*;

public class SimuladorPaginacao {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite a sequência de páginas (separadas por espaço): ");
        String[] input = scanner.nextLine().split(" ");
        int[] paginas = Arrays.stream(input).mapToInt(Integer::parseInt).toArray();

        System.out.print("Digite a quantidade de quadros de memória: ");
        int quadros = scanner.nextInt();

        System.out.println("\nResultados:");
        System.out.println("FIFO - " + fifo(paginas, quadros) + " faltas de página");
        System.out.println("LRU - " + lru(paginas, quadros) + " faltas de página");
        System.out.println("Relógio - " + clock(paginas, quadros) + " faltas de página");
        System.out.println("Envelhecimento - " + aging(paginas, quadros) + " faltas de página");

    }

    // Algoritmo FIFO
    public static int fifo(int[] paginas, int quadros) {
        Set<Integer> memoria = new HashSet<>();
        Queue<Integer> fila = new LinkedList<>();
        int faltas = 0;

        for (int pagina : paginas) {
            if (!memoria.contains(pagina)) {
                if (memoria.size() == quadros) {
                    int removida = fila.poll();
                    memoria.remove(removida);
                }
                memoria.add(pagina);
                fila.add(pagina);
                faltas++;
            }
        }
        return faltas;
    }

    // Algoritmo LRU
    public static int lru(int[] paginas, int quadros) {
        Map<Integer, Integer> memoria = new LinkedHashMap<>(quadros, 0.75f, true);
        int faltas = 0;

        for (int pagina : paginas) {
            if (!memoria.containsKey(pagina)) {
                if (memoria.size() == quadros) {
                    int lru = memoria.keySet().iterator().next();
                    memoria.remove(lru);
                }
                faltas++;
            }
            memoria.put(pagina, 1);
        }
        return faltas;
    }

    // Algoritmo Relógio (Clock)
    public static int clock(int[] paginas, int quadros) {
        int[] quadro = new int[quadros];
        boolean[] uso = new boolean[quadros];
        Arrays.fill(quadro, -1);
        int ponteiro = 0;
        int faltas = 0;

        for (int pagina : paginas) {
            boolean encontrado = false;

            for (int i = 0; i < quadros; i++) {
                if (quadro[i] == pagina) {
                    uso[i] = true;
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                while (uso[ponteiro]) {
                    uso[ponteiro] = false;
                    ponteiro = (ponteiro + 1) % quadros;
                }

                quadro[ponteiro] = pagina;
                uso[ponteiro] = true;
                ponteiro = (ponteiro + 1) % quadros;
                faltas++;
            }
        }

        return faltas;
    }

    // Algoritmo de Envelhecimento (Aging)
    public static int aging(int[] paginas, int quadros) {
        Map<Integer, Integer> memoria = new LinkedHashMap<>();
        Map<Integer, Byte> agingBits = new HashMap<>();
        int faltas = 0;

        for (int pagina : paginas) {
            for (Integer p : agingBits.keySet()) {
                agingBits.put(p, (byte) (agingBits.get(p) >> 1));
            }

            if (memoria.containsKey(pagina)) {
                agingBits.put(pagina, (byte) (agingBits.get(pagina) | 0b10000000));
            } else {
                if (memoria.size() == quadros) {
                    int menosUsada = Collections.min(agingBits.entrySet(), Map.Entry.comparingByValue()).getKey();
                    memoria.remove(menosUsada);
                    agingBits.remove(menosUsada);
                }
                memoria.put(pagina, 1);
                agingBits.put(pagina, (byte) 0b10000000);
                faltas++;
            }
        }

        return faltas;
    }
}
