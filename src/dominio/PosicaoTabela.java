package dominio;

public record PosicaoTabela(Time time,
                            Long vitorias,
                            Long derrotas,
                            Long empates,
                            Long golsPositivos,
                            Long golsSofridos,
                            Long saldoDeGols,
                            Long jogos) implements Comparable<PosicaoTabela>{
    public Long pontos(){
        return (vitorias * 3 + empates);
    }
    @Override
    public String toString() {
        return  time +
                ", pontos=" + pontos() +
                ", vitorias=" + vitorias +
                ", derrotas=" + derrotas +
                ", empates=" + empates +
                ", golsPositivos=" + golsPositivos +
                ", golsSofridos=" + golsSofridos +
                ", saldoDeGols=" + saldoDeGols +
                ", jogos=" + jogos +
                '}';
    }


    public int compareTo(PosicaoTabela posicao) {
        if (posicao.pontos() == pontos()) {
            if (posicao.vitorias == vitorias())
                return Long.compare(posicao.saldoDeGols, saldoDeGols);
            else
                return Long.compare(posicao.vitorias, vitorias);
        } else
            return Long.compare(posicao.pontos(), pontos());
    }
}


