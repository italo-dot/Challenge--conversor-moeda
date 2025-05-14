package desafio.conversor.principal;



import desafio.conversor.dados.DadosCambio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;

import java.util.Scanner;

public class ConversorMoedas {

     static String API_KEY = "7179fbca24d71cc5d51d93c3/";
     static String API_URL = "https://v6.exchangerate-api.com/v6/";
     static HttpClient client = HttpClient.newHttpClient();
    static Gson gson = new GsonBuilder().create();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            exibirMenu();
            System.out.print("Escolha uma opção de conversão (ou 0 para sair): ");
            try {
                opcao = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                scanner.next();
                opcao = -1;
                continue;
            }
            scanner.nextLine();

            if (opcao >= 1 && opcao <= 6) {
                realizarConversao(opcao, scanner);
            } else if (opcao != 0) {
                System.out.println("Opção inválida. Tente novamente.");
            }

        } while (opcao != 0);

        System.out.println("Programa encerrado.");
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n--- Conversor de Moedas ---");
        System.out.println("1. Dólar (USD) para Real (BRL)");
        System.out.println("2. Real (BRL) para Dólar (USD)");
        System.out.println("3. Dólar (USD) para Euro (EUR)");
        System.out.println("4. Euro (EUR) para Dólar (USD)");
        System.out.println("5. Real (BRL) para Euro (EUR)");
        System.out.println("6. Euro (EUR) para Real (BRL)");
        System.out.println("0. Sair");
    }

    private static void realizarConversao(int opcao, Scanner scanner) {
        String moedaBase = "";
        String moedaDestino = "";

        switch (opcao) {
            case 1 -> {
                moedaBase = "USD";
                moedaDestino = "BRL";
            }
            case 2 -> {
                moedaBase = "BRL";
                moedaDestino = "USD";
            }
            case 3 -> {
                moedaBase = "USD";
                moedaDestino = "EUR";
            }
            case 4 -> {
                moedaBase = "EUR";
                moedaDestino = "USD";
            }
            case 5 -> {
                moedaBase = "BRL";
                moedaDestino = "EUR";
            }
            case 6 -> {
                moedaBase = "EUR";
                moedaDestino = "BRL";
            }
            default -> System.out.println("Opção inválida.");
        }



        System.out.print("Digite o valor em " + obterNomeMoeda(moedaBase) + " a ser convertido para " + obterNomeMoeda(moedaDestino) + ": ");
        double valor;
        try {
            valor = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Valor inválido. Por favor, digite um número.");
            scanner.next();
            return;
        }
        scanner.nextLine();

        try {
            double taxa = obterTaxaDeCambio(moedaBase, moedaDestino);
            double resultado = valor * taxa;
            System.out.printf("%.2f %s equivalem a %.2f %s\n", valor, obterNomeMoeda(moedaBase), resultado, obterNomeMoeda(moedaDestino));
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao obter a taxa de câmbio: " + e.getMessage());
        }
    }

    private static double obterTaxaDeCambio(String moedaBase, String moedaDestino) throws IOException, InterruptedException {
        URI uri = URI.create(API_URL + API_KEY + "latest/" + moedaBase);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonResponse = response.body();

        DadosCambio data = gson.fromJson(jsonResponse, DadosCambio.class);


        if (data != null && "success".equals(data.getResult()) && data.getConversionRates() != null && data.getConversionRates().containsKey(moedaDestino)) {
            return data.getConversionRates().get(moedaDestino);
        } else {
            throw new IOException("Não foi possível obter a taxa de câmbio para " + moedaDestino + " a partir de " + moedaBase + ". Resposta da API: " + jsonResponse);
        }
    }

    private static String obterNomeMoeda(String codigoMoeda) {
        return switch (codigoMoeda) {
            case "USD" -> "Dólar";
            case "BRL" -> "Real";
            case "EUR" -> "Euro";
            default -> codigoMoeda;
        };
    }
}