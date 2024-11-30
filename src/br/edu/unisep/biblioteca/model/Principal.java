package br.edu.unisep.biblioteca.model;

import javax.swing.*;
import java.util.ArrayList;

public class Principal {
    private static final ArrayList<Livro> catalogoLivros = new ArrayList<>();
    private static final ArrayList<Autor> autoresRegistrados = new ArrayList<>();
    private static final ArrayList<Genero> generosDisponiveis = new ArrayList<>();
    private static final ArrayList<Usuario> usuariosAtivos = new ArrayList<>();
    private static final ArrayList<Emprestimo> emprestimosRealizados = new ArrayList<>();

    public static void main(String[] args) {
        boolean continuar = true;

        while (continuar) {
            String menu = """
                    Sistema de Biblioteca
                    ---------------------
                    1. Adicionar Livro
                    2. Registrar Autor
                    3. Adicionar Gênero
                    4. Registrar Usuário
                    5. Fazer Empréstimo
                    6. Registrar Devolução
                    7. Verificar Livros Disponíveis
                    8. Encerrar Sistema
                    """;

            String entrada = JOptionPane.showInputDialog(menu);

            if (entrada == null) {
                continuar = false;
                break;
            }

            try {
                int escolha = Integer.parseInt(entrada);

                switch (escolha) {
                    case 1 -> adicionarLivro();
                    case 2 -> registrarAutor();
                    case 3 -> adicionarGenero();
                    case 4 -> registrarUsuario();
                    case 5 -> realizarEmprestimo();
                    case 6 -> registrarDevolucao();
                    case 7 -> listarLivrosDisponiveis();
                    case 8 -> {
                        JOptionPane.showMessageDialog(null, "Encerrando o sistema.");
                        continuar = false;
                    }
                    default -> JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Insira apenas números.");
            }
        }
    }

    private static void adicionarLivro() {
        String titulo = JOptionPane.showInputDialog("Informe o título do livro:");
        if (titulo == null || titulo.isBlank()) return;

        Autor autor = escolherAutor();
        if (autor == null) {
            JOptionPane.showMessageDialog(null, "Nenhum autor registrado. Cadastre um autor primeiro.");
            return;
        }

        Genero genero = escolherGenero();
        if (genero == null) {
            JOptionPane.showMessageDialog(null, "Nenhum gênero cadastrado. Adicione gêneros primeiro.");
            return;
        }

        String tipo = JOptionPane.showInputDialog("Tipo do Livro:\n1. Físico\n2. Digital");
        if ("1".equals(tipo)) {
            int exemplares = Integer.parseInt(JOptionPane.showInputDialog("Quantidade de Exemplares:"));
            catalogoLivros.add(new LivroFisico(titulo, autor, genero, exemplares));
        } else if ("2".equals(tipo)) {
            double tamanhoArquivo = Double.parseDouble(JOptionPane.showInputDialog("Tamanho do Arquivo (em MB):"));
            catalogoLivros.add(new LivroDigital(titulo, autor, genero, tamanhoArquivo));
        } else {
            JOptionPane.showMessageDialog(null, "Opção de tipo inválida. Livro não adicionado.");
        }
    }

    private static void registrarAutor() {
        String nomeAutor = JOptionPane.showInputDialog("Digite o nome do autor:");
        if (nomeAutor != null && !nomeAutor.isBlank()) {
            autoresRegistrados.add(new Autor(nomeAutor));
            JOptionPane.showMessageDialog(null, "Autor registrado com sucesso.");
        }
    }

    private static void adicionarGenero() {
        String nomeGenero = JOptionPane.showInputDialog("Digite o nome do gênero:");
        if (nomeGenero != null && !nomeGenero.isBlank()) {
            generosDisponiveis.add(new Genero(nomeGenero));
            JOptionPane.showMessageDialog(null, "Gênero adicionado com sucesso.");
        }
    }

    private static void registrarUsuario() {
        String nomeUsuario = JOptionPane.showInputDialog("Digite o nome do usuário:");
        if (nomeUsuario != null && !nomeUsuario.isBlank()) {
            usuariosAtivos.add(new Usuario(nomeUsuario));
            JOptionPane.showMessageDialog(null, "Usuário registrado com sucesso.");
        }
    }

    private static void realizarEmprestimo() {
        if (catalogoLivros.isEmpty() || usuariosAtivos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum livro ou usuário disponível para empréstimos.");
            return;
        }

        String[] titulosDisponiveis = catalogoLivros.stream()
                .filter(Livro::isDisponivel)
                .map(Livro::getTitulo)
                .toArray(String[]::new);

        if (titulosDisponiveis.length == 0) {
            JOptionPane.showMessageDialog(null, "Todos os livros estão emprestados.");
            return;
        }

        String livroEscolhido = (String) JOptionPane.showInputDialog(null, "Escolha um Livro:",
                "Livros Disponíveis", JOptionPane.QUESTION_MESSAGE, null, titulosDisponiveis, titulosDisponiveis[0]);

        String[] usuarios = usuariosAtivos.stream().map(Usuario::getNome).toArray(String[]::new);

        String usuarioEscolhido = (String) JOptionPane.showInputDialog(null, "Escolha um Usuário:",
                "Usuários", JOptionPane.QUESTION_MESSAGE, null, usuarios, usuarios[0]);

        Livro livro = catalogoLivros.stream().filter(l -> l.getTitulo().equals(livroEscolhido)).findFirst().orElse(null);
        Usuario usuario = usuariosAtivos.stream().filter(u -> u.getNome().equals(usuarioEscolhido)).findFirst().orElse(null);

        if (livro != null && usuario != null) {
            livro.setDisponivel(false);
            emprestimosRealizados.add(new Emprestimo(livro, usuario));
            JOptionPane.showMessageDialog(null, "Empréstimo realizado com sucesso.");
        }
    }

    private static void registrarDevolucao() {
        if (emprestimosRealizados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum empréstimo pendente.");
            return;
        }

        String[] pendentes = emprestimosRealizados.stream()
                .filter(e -> e.getDataDevolucao() == null)
                .map(e -> e.getLivro().getTitulo() + " - " + e.getUsuario().getNome())
                .toArray(String[]::new);

        if (pendentes.length == 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma devolução pendente.");
            return;
        }

        String devolucaoEscolhida = (String) JOptionPane.showInputDialog(null, "Escolha um Empréstimo para Devolução:",
                "Devoluções", JOptionPane.QUESTION_MESSAGE, null, pendentes, pendentes[0]);

        Emprestimo emprestimo = emprestimosRealizados.stream()
                .filter(e -> (e.getLivro().getTitulo() + " - " + e.getUsuario().getNome()).equals(devolucaoEscolhida))
                .findFirst()
                .orElse(null);

        if (emprestimo != null) {
            emprestimo.devolver();
            JOptionPane.showMessageDialog(null, "Livro devolvido com sucesso.");
        }
    }

    private static void listarLivrosDisponiveis() {
        StringBuilder lista = new StringBuilder("Livros Disponíveis:\n");
        catalogoLivros.stream().filter(Livro::isDisponivel).forEach(l -> lista.append(l).append("\n"));
        JOptionPane.showMessageDialog(null, lista.length() > 0 ? lista.toString() : "Nenhum livro disponível.");
    }

    private static Autor escolherAutor() {
        if (autoresRegistrados.isEmpty()) return null;

        String[] nomesAutores = autoresRegistrados.stream().map(Autor::getNome).toArray(String[]::new);
        String escolhido = (String) JOptionPane.showInputDialog(null, "Selecione um Autor:",
                "Autores", JOptionPane.QUESTION_MESSAGE, null, nomesAutores, nomesAutores[0]);
        return autoresRegistrados.stream().filter(a -> a.getNome().equals(escolhido)).findFirst().orElse(null);
    }

    private static Genero escolherGenero() {
        if (generosDisponiveis.isEmpty()) return null;

        String[] nomesGeneros = generosDisponiveis.stream().map(Genero::getNome).toArray(String[]::new);
        String escolhido = (String) JOptionPane.showInputDialog(null, "Selecione um Gênero:",
                "Gêneros", JOptionPane.QUESTION_MESSAGE, null, nomesGeneros, nomesGeneros[0]);
        return generosDisponiveis.stream().filter(g -> g.getNome().equals(escolhido)).findFirst().orElse(null);
    }
}
