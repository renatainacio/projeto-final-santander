import dominio.Resultado;
import dominio.Time;
import impl.CampeonatoBrasileiroImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            Path path = Path.of("campeonato-brasileiro.csv");
            CampeonatoBrasileiroImpl cb = new CampeonatoBrasileiroImpl(path, (jogo) -> jogo.data().data().getYear() == 2018);
            System.out.println("Número de jogos: " + cb.getEstatisticasPorJogo().getCount());
            System.out.println("Total de Gols: " + cb.getEstatisticasPorJogo().getSum());
            System.out.printf("Média de Gols: %.2f%n", cb.getEstatisticasPorJogo().getAverage());
            System.out.println("Mínimo de Golss: " + cb.getEstatisticasPorJogo().getMin());
            System.out.println("Máximo de Golss: " + cb.getEstatisticasPorJogo().getMax());
            System.out.println("Número de vitórias em casa: " + cb.getTotalVitoriasEmCasa());
            System.out.println("Número de vitórias fora de casa: " + cb.getTotalVitoriasForaDeCasa());
            System.out.println("Número de empates: " + cb.getTotalEmpates());
            System.out.println("Número Jogos com menos de 3 Gols: " + cb.getTotalJogosComMenosDe3Gols());
            System.out.println("Número Jogos com 3 ou mais Gols: " + cb.getTotalJogosCom3OuMaisGols());
            System.out.println("Placar mais repetido: " + cb.getPlacarMaisRepetido().getKey() +
                    " - " + cb.getPlacarMaisRepetido().getValue() + " jogos");
            System.out.println("Placar menos repetido: " + cb.getPlacarMenosRepetido().getKey() +
                    " - " + cb.getPlacarMenosRepetido().getValue() + " jogo(s)");
            System.out.println("Todos os placares: ");
            for (Map.Entry<Resultado, Long> placar : cb.getTodosOsPlacares().entrySet()) {
                System.out.println(placar.getKey() + ": " + placar.getValue() + " jogos");
            }
            System.out.println("Média Gols por Rodada");
            System.out.println(cb.getMediaDeGolsPorRodada());
            System.out.println("Total Gols por Rodada");
            System.out.println(cb.getTotalGolsPorRodada());
            System.out.println();
            System.out.println("Tabela:");
            for(int i = 1; i <= cb.getTodosOsTimes().size(); i++)
            System.out.println(i + ". " + cb.getTabelaOrdenada().get(i - 1));
        }
        catch (IOException e){
            System.out.println("Não foi possível ler o arquivo especificado.");
        }
        }
}
