package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;
import ch.qos.logback.core.encoder.JsonEscapeUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
       var menu = """
               *** OPÇÔES ***
               Carro
               Moto
               Caminhão
               
               Digite uma das opções para consultar:
               """;

        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("carro")) {
            endereco = URL_BASE + "carros/marcas";
        }else if(opcao.toLowerCase().contains("moto")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        var menu2 = """
                Escolha uma das marcas pelo seu respectivo código para consultar os modelos:
                """;
        System.out.println(menu2);
        var codMarca = leitura.nextLine();

        endereco = endereco + "/" + codMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);
        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        System.out.println("Digite o nome do carro a ser buscado");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println(modelosFiltrados);
        modelosFiltrados.forEach(System.out::println);
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);

            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);
    }

}
