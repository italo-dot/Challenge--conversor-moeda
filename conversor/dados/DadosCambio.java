package desafio.conversor.dados;
import java.util.Map;


public class DadosCambio {
    private String result;
    private Map<String, Double> conversion_rates;

    public String getResult() {
        return result;
    }

    public Map<String, Double> getConversionRates() {
        return conversion_rates;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setConversionRates(Map<String, Double> conversion_rates) {
        this.conversion_rates = conversion_rates;
    }
}