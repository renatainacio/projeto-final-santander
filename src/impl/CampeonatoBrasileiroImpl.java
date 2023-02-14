package impl;

import dominio.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CampeonatoBrasileiroImpl {

    private List<Jogo> brasileirao;
    private List<Jogo> jogos;

    private Predicate<Jogo> filtro;

    public CampeonatoBrasileiroImpl(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro)
                .toList();
    }

    public List<Jogo> lerArquivo(Path arquivo) throws IOException {
        List<Jogo> listaJogos = new ArrayList<>();
        List<String> dadosArquivo = Files.readAllLines(arquivo);
        dadosArquivo.remove(0);
        for (String line : dadosArquivo)
        {
            String[] linha = line.split(";");
            Integer rodada = Integer.parseInt(linha[0]);
            DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataJogo = LocalDate.parse(linha[1], formatadorData);
            LocalTime horaJogo;
            DateTimeFormatter formatadorHora1 = DateTimeFormatter.ofPattern("HH'h'mm");
            DateTimeFormatter formatadorHora2 = DateTimeFormatter.ofPattern("HH:mm");
            try{
                horaJogo = LocalTime.parse(linha[2], formatadorHora1);
            } catch (Exception e1 ) {
                try{
                    horaJogo = LocalTime.parse(linha[2], formatadorHora2);
                } catch(Exception e2) {
                    horaJogo = null;
                }
                }
            DataDoJogo data = new DataDoJogo(dataJogo, horaJogo, dataJogo.getDayOfWeek());
            Time mandante = new Time(linha[4]);
            Time visitante = new Time(linha[5]);
            Time vencedor = new Time(linha[6]);
            String arena = linha[7];
            Integer mandantePlacar = Integer.parseInt(linha[8]);
            Integer visitantePlacar = Integer.parseInt(linha[9]);
            String estadoMandante = linha[10];
            String estadoVisitante = linha [11];
            String estadoVencedor = linha[12];
            listaJogos.add(new Jogo(rodada, data, mandante, visitante, vencedor, arena, mandantePlacar, visitantePlacar, estadoMandante, estadoVisitante, estadoVencedor));
        }
        return listaJogos;
    }

    public IntSummaryStatistics getEstatisticasPorJogo() {
        IntStream stats = this.brasileirao
                .stream()
                .mapToInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar());
        return stats.summaryStatistics();
    }

    public List<Jogo> todosOsJogos() {
        return this.brasileirao;
    }

    public Long getTotalVitoriasEmCasa() {
        Long vitoriasEmCasa = this.brasileirao
                .stream()
                .filter(jogo -> jogo.mandante().equals(jogo.vencedor()))
                .count();
        return vitoriasEmCasa;
    }

    public Long getTotalVitoriasForaDeCasa() {
        Long vitoriasForaCasa = this.brasileirao
                .stream()
                .filter(jogo -> jogo.visitante().equals(jogo.vencedor()))
                .count();
        return vitoriasForaCasa;
    }

    public Long getTotalEmpates() {
        Long empates = this.brasileirao
                .stream()
                .filter(jogo -> jogo.vencedor().toString().equals("-"))
                .count();
        return empates;
    }

    public Long getTotalJogosComMenosDe3Gols() {
        Long jogosMenosTresGols = this.brasileirao
                .stream()
                .filter(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() < 3)
                .count();
        return jogosMenosTresGols;
    }

    public Long getTotalJogosCom3OuMaisGols() {
        Long jogosTresGolsOuMais = this.brasileirao
                .stream()
                .filter(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar() >= 3)
                .count();
        return jogosTresGolsOuMais;
    }

    public Map<Resultado, Long> getTodosOsPlacares() {
        Map<Resultado, Long> placares = this.brasileirao
                .stream()
                .collect(Collectors.groupingBy(jogo-> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()), Collectors.counting()));
        return placares;
    }

    public Map.Entry<Resultado, Long> getPlacarMaisRepetido() {
        Map.Entry<Resultado, Long> placarMaisRepetido = null;
        Long repeticaoPlacar = 0L;
        for (Map.Entry<Resultado, Long> placar : getTodosOsPlacares().entrySet()) {
            if(placar.getValue() > repeticaoPlacar) {
                placarMaisRepetido = placar;
                repeticaoPlacar = placar.getValue();
            }
        }
        return placarMaisRepetido;
    }

    public Map.Entry<Resultado, Long> getPlacarMenosRepetido() {
        Map.Entry<Resultado, Long> placarMenosRepetido = null;
        Long repeticaoPlacar = getEstatisticasPorJogo().getCount();
        for (Map.Entry<Resultado, Long> placar : getTodosOsPlacares().entrySet()) {
            if(placar.getValue() < repeticaoPlacar) {
                placarMenosRepetido = placar;
                repeticaoPlacar = placar.getValue();
            }
        }
        return placarMenosRepetido;
    }
    public List<Time> getTodosOsTimes() {
        List<Time> times = this.brasileirao
                .stream()
                .map(jogo->jogo.mandante())
                .distinct()
                .toList();
        return times;
    }

    public Map<Time, List<Jogo>> getTodosOsJogosPorTimeComoMandantes() {
        Map<Time, List<Jogo>> jogosPorTimeComoMandantes = new HashMap<>();
        for(Time time : getTodosOsTimes()){
            List <Jogo> jogosComoMandante = this.brasileirao
                    .stream()
                    .filter(jogo -> jogo.mandante().equals(time))
                    .toList();
            jogosPorTimeComoMandantes.put(time, jogosComoMandante);
        }
        return(jogosPorTimeComoMandantes);
    }

    public Map<Time, List<Jogo>> getTodosOsJogosPorTimeComoVisitante() {
        Map<Time, List<Jogo>> jogosPorTimeComoVisitante = new HashMap<>();
        for(Time time : getTodosOsTimes()){
            List <Jogo> jogosComoVisitante = this.brasileirao
                    .stream()
                    .filter(jogo -> jogo.visitante().equals(time))
                    .toList();
            jogosPorTimeComoVisitante.put(time, jogosComoVisitante);
        }
        return(jogosPorTimeComoVisitante);
    }

    public Map<Time, List<Jogo>> getTodosOsJogosPorTime() {
        Map<Time, List<Jogo>> jogosPorTime = new HashMap<>();
        for(Time time : getTodosOsTimes()) {
            List<Jogo> todos = new ArrayList<>();
            todos.addAll(getTodosOsJogosPorTimeComoVisitante().get(time));
            todos.addAll(getTodosOsJogosPorTimeComoMandantes().get(time));
            jogosPorTime.put(time, todos);
        }
        return(jogosPorTime);
    }
    public Set<PosicaoTabela> getTabela() {
        Set<PosicaoTabela> tabela = new HashSet<>();
        Set<Time> times = new HashSet<>(getTodosOsTimes());
        for(Time time : times){
            List<Jogo> jogos = getTodosOsJogosPorTime().get(time);
            Long vitorias = jogos
                    .stream()
                    .filter(jogo->jogo.vencedor().equals(time))
                    .count();
            Long empates = jogos
                    .stream()
                    .filter(jogo->jogo.vencedor().toString().equals("-"))
                    .count();
            Long derrotas = jogos
                    .stream()
                    .count() - vitorias - empates;
            Long golsMarcados = getTotalDeGolsPorTime().get(time);
            Long golsSofridos = getGolsSofridosPorTime().get(time);
            Long saldoGols = golsMarcados - golsSofridos;
            Long numeroJogos = (long)jogos.size();
            tabela.add(new PosicaoTabela(time, vitorias, derrotas, empates, golsMarcados, golsSofridos, saldoGols, numeroJogos));
        }
        return tabela;
    }

    public List<PosicaoTabela> getTabelaOrdenada(){
        return getTabela().stream().sorted().toList();
    }

    private List<Integer> getRodadas(){
        List<Integer> rodadas = this.brasileirao
                .stream()
                .map(jogo -> jogo.rodada())
                .distinct()
                .toList();
        return rodadas;
    }
    public Map<Integer, Long> getTotalGolsPorRodada() {
        Map<Integer, Long> totalGolsPorRodada = new HashMap<>();
        List<Integer> rodadas = getRodadas();
        for(Integer i : rodadas)
        {
            Long totalGols = this.brasileirao
                    .stream()
                    .filter(jogo -> jogo.rodada().equals(i))
                    .mapToInt(jogo-> jogo.mandantePlacar()+jogo.visitantePlacar())
                    .summaryStatistics().getSum();
            totalGolsPorRodada.put(i, totalGols);
        }
        return totalGolsPorRodada;
    }

    private Map<Time, Long> getTotalDeGolsPorTime() {
        Map<Time, Long> totalGolsPorTime = new HashMap<>();
        for(Time time : getTodosOsTimes()){
            Long totalGols = this.brasileirao
                    .stream()
                    .filter(jogo -> jogo.visitante().equals(time))
                    .mapToInt(jogo -> jogo.visitantePlacar())
                    .summaryStatistics().getSum() +
                    this.brasileirao
                                    .stream()
                                            .filter(jogo -> jogo.mandante().equals(time))
                                                    .mapToInt(jogo->jogo.mandantePlacar())
                                                            .summaryStatistics().getSum();
            totalGolsPorTime.put(time, totalGols);
        }
        return totalGolsPorTime;
    }

    public Map<Time, Long> getGolsSofridosPorTime(){
        Map<Time, Long> totalGolsSofridosPorTime = new HashMap<>();
        for(Time time : getTodosOsTimes()){
            Long totalGolsSofridos = this.brasileirao
                    .stream()
                    .filter(jogo -> jogo.visitante().equals(time))
                    .mapToInt(jogo -> jogo.mandantePlacar())
                    .summaryStatistics().getSum() +
                    this.brasileirao
                            .stream()
                            .filter(jogo -> jogo.mandante().equals(time))
                            .mapToInt(jogo->jogo.visitantePlacar())
                            .summaryStatistics().getSum();
            totalGolsSofridosPorTime.put(time, totalGolsSofridos);
        }
        return totalGolsSofridosPorTime;
    }

    public Map<Integer, Double> getMediaDeGolsPorRodada() {
        Map<Integer, Double> mediaGolsPorRodada = new HashMap<>();
        List<Integer> rodadas = getRodadas();
        for(Integer i : rodadas)
        {
            Double mediaGols = this.brasileirao
                            .stream()
                                    .filter(jogo -> jogo.rodada().equals(i))
                                            .mapToInt(jogo-> jogo.mandantePlacar()+jogo.visitantePlacar())
                                                    .summaryStatistics().getAverage();
            mediaGolsPorRodada.put(i, mediaGols);
        }
        return mediaGolsPorRodada;
    }


}