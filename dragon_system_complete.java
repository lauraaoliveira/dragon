// ==================== ENUMS ====================

// enums/Elemento.java
package enums;

public enum Elemento {
    FOGO, GELO, RAIO;
    
    public boolean temVantagemContra(Elemento outro) {
        return (this == FOGO && outro == GELO) ||
               (this == GELO && outro == RAIO) ||
               (this == RAIO && outro == FOGO);
    }
}

// enums/StatusDragao.java
package enums;

public enum StatusDragao {
    SAUDAVEL, FERIDO, DERROTADO
}

// ==================== EXCEPTIONS ====================

// exceptions/DragaoInvalidoException.java
package exceptions;

public class DragaoInvalidoException extends Exception {
    public DragaoInvalidoException(String mensagem) {
        super(mensagem);
    }
}

// exceptions/EnergiaInsuficienteException.java
package exceptions;

public class EnergiaInsuficienteException extends Exception {
    public EnergiaInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

// exceptions/TreinadorSemEspacoException.java
package exceptions;

public class TreinadorSemEspacoException extends Exception {
    public TreinadorSemEspacoException(String mensagem) {
        super(mensagem);
    }
}

// ==================== INTERFACE ====================

// interfaces/ICombate.java
package interfaces;

import exceptions.EnergiaInsuficienteException;

public interface ICombate {
    int atacar() throws EnergiaInsuficienteException;
    void defender(int dano);
}

// ==================== ABSTRACT CLASS ====================

// abstracts/Criatura.java
package abstracts;

import enums.StatusDragao;

public abstract class Criatura {
    protected String nome;
    protected int vida;
    protected int energia;
    protected StatusDragao status;
    
    public Criatura(String nome, int vida) {
        this.nome = nome;
        this.vida = vida;
        this.energia = 100;
        this.status = StatusDragao.SAUDAVEL;
    }
    
    public abstract void exibirInfo();
    
    public void receberDano(int dano) {
        this.vida -= dano;
        if (this.vida <= 0) {
            this.vida = 0;
            this.status = StatusDragao.DERROTADO;
        } else if (this.vida <= 30) {
            this.status = StatusDragao.FERIDO;
        }
    }
    
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getEnergia() { return energia; }
    public StatusDragao getStatus() { return status; }
    public boolean estaVivo() { return vida > 0; }
}

// ==================== MODELS ====================

// models/Dragao.java
package models;

import abstracts.Criatura;
import enums.Elemento;
import interfaces.ICombate;
import exceptions.EnergiaInsuficienteException;

public abstract class Dragao extends Criatura implements ICombate {
    protected Elemento elemento;
    protected int poder;
    
    // Construtor padrão
    public Dragao(String nome, Elemento elemento) {
        super(nome, 100);
        this.elemento = elemento;
        this.poder = 20;
    }
    
    // Construtor sobrecarregado
    public Dragao(String nome, Elemento elemento, int poder) {
        super(nome, 100);
        this.elemento = elemento;
        this.poder = poder;
    }
    
    // Polimorfismo de método - sobrecarga
    @Override
    public int atacar() throws EnergiaInsuficienteException {
        if (energia < 15) {
            throw new EnergiaInsuficienteException(nome + " sem energia!");
        }
        energia -= 15;
        return poder;
    }
    
    public int atacar(Dragao oponente) throws EnergiaInsuficienteException {
        int dano = atacar();
        if (this.elemento.temVantagemContra(oponente.getElemento())) {
            return (int)(dano * 1.5);
        }
        return dano;
    }
    
    @Override
    public void defender(int dano) {
        receberDano((int)(dano * 0.7));
    }
    
    @Override
    public void exibirInfo() {
        System.out.println(nome + " (" + elemento + ") - Vida: " + vida + " | Energia: " + energia);
    }
    
    public Elemento getElemento() { return elemento; }
}

// models/DragaoDeFogo.java
package models;

import enums.Elemento;

public class DragaoDeFogo extends Dragao {
    public DragaoDeFogo(String nome) {
        super(nome, Elemento.FOGO, 25); // Mais forte
    }
}

// models/DragaoDeGelo.java
package models;

import enums.Elemento;

public class DragaoDeGelo extends Dragao {
    public DragaoDeGelo(String nome) {
        super(nome, Elemento.GELO);
        this.vida = 120; // Mais vida
    }
}

// models/DragaoDeRaio.java
package models;

import enums.Elemento;

public class DragaoDeRaio extends Dragao {
    public DragaoDeRaio(String nome) {
        super(nome, Elemento.RAIO);
        this.energia = 130; // Mais energia
    }
}

// models/Treinador.java
package models;

import exceptions.TreinadorSemEspacoException;
import java.util.ArrayList;
import java.util.List;

public class Treinador {
    private String nome;
    private List<Dragao> dragoes;
    private static final int LIMITE = 6;
    
    public Treinador(String nome) {
        this.nome = nome;
        this.dragoes = new ArrayList<>();
    }
    
    public void adicionarDragao(Dragao dragao) throws TreinadorSemEspacoException {
        if (dragoes.size() >= LIMITE) {
            throw new TreinadorSemEspacoException("Limite de dragoes atingido!");
        }
        dragoes.add(dragao);
    }
    
    public void listarDragoes() {
        System.out.println("\nTreinador: " + nome);
        for (int i = 0; i < dragoes.size(); i++) {
            System.out.print((i + 1) + ". ");
            dragoes.get(i).exibirInfo();
        }
    }
    
    public String getNome() { return nome; }
    public List<Dragao> getDragoes() { return dragoes; }
}

// models/Arena.java
package models;

import exceptions.EnergiaInsuficienteException;

public class Arena {
    
    public void iniciarBatalha(Dragao d1, Dragao d2) {
        System.out.println("\n=== BATALHA ===");
        System.out.println(d1.getNome() + " VS " + d2.getNome());
        
        int turno = 1;
        while (d1.estaVivo() && d2.estaVivo()) {
            System.out.println("\n--- Turno " + turno + " ---");
            
            // D1 ataca
            try {
                int dano = d1.atacar(d2);
                d2.receberDano(dano);
                System.out.println(d1.getNome() + " causou " + dano + " de dano");
            } catch (EnergiaInsuficienteException e) {
                System.out.println(e.getMessage());
                d1.energia += 30; // Recupera
            }
            
            if (!d2.estaVivo()) break;
            
            // D2 ataca
            try {
                int dano = d2.atacar(d1);
                d1.receberDano(dano);
                System.out.println(d2.getNome() + " causou " + dano + " de dano");
            } catch (EnergiaInsuficienteException e) {
                System.out.println(e.getMessage());
                d2.energia += 30;
            }
            
            turno++;
        }
        
        Dragao vencedor = d1.estaVivo() ? d1 : d2;
        System.out.println("\n=== VENCEDOR: " + vencedor.getNome() + " ===\n");
    }
}

// ==================== MAIN ====================

// Main.java
import models.*;
import exceptions.*;
import java.util.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static List<Treinador> treinadores = new ArrayList<>();
    private static Arena arena = new Arena();

    public static void main(String[] args) {
        int opcao;
        
        do {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Criar Treinador");
            System.out.println("2. Adicionar Dragao");
            System.out.println("3. Listar Treinadores");
            System.out.println("4. Batalhar");
            System.out.println("0. Sair");
            System.out.print("Opcao: ");
            opcao = sc.nextInt();
            sc.nextLine();
            
            switch (opcao) {
                case 1 -> criarTreinador();
                case 2 -> criarDragao();
                case 3 -> listarTreinadores();
                case 4 -> batalhar();
            }
        } while (opcao != 0);
    }
    
    private static void criarTreinador() {
        System.out.print("Nome do treinador: ");
        String nome = sc.nextLine();
        treinadores.add(new Treinador(nome));
        System.out.println("Treinador criado!");
    }
    
    private static void criarDragao() {
        if (treinadores.isEmpty()) {
            System.out.println("Crie um treinador primeiro!");
            return;
        }
        
        try {
            System.out.println("\nTreinadores:");
            for (int i = 0; i < treinadores.size(); i++) {
                System.out.println((i + 1) + ". " + treinadores.get(i).getNome());
            }
            System.out.print("Escolha: ");
            int t = sc.nextInt() - 1;
            sc.nextLine();
            
            System.out.print("Nome do dragao: ");
            String nome = sc.nextLine();
            
            System.out.println("1. Fogo  2. Gelo  3. Raio");
            System.out.print("Tipo: ");
            int tipo = sc.nextInt();
            sc.nextLine();
            
            Dragao dragao = switch (tipo) {
                case 1 -> new DragaoDeFogo(nome);
                case 2 -> new DragaoDeGelo(nome);
                case 3 -> new DragaoDeRaio(nome);
                default -> null;
            };
            
            if (dragao != null) {
                treinadores.get(t).adicionarDragao(dragao);
                System.out.println("Dragao adicionado!");
            }
        } catch (TreinadorSemEspacoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    
    private static void listarTreinadores() {
        for (Treinador t : treinadores) {
            t.listarDragoes();
        }
    }
    
    private static void batalhar() {
        if (treinadores.size() < 2) {
            System.out.println("Precisa de 2 treinadores!");
            return;
        }
        
        System.out.print("Treinador 1 (1-" + treinadores.size() + "): ");
        int t1 = sc.nextInt() - 1;
        System.out.print("Dragao (1-" + treinadores.get(t1).getDragoes().size() + "): ");
        int d1 = sc.nextInt() - 1;
        
        System.out.print("Treinador 2 (1-" + treinadores.size() + "): ");
        int t2 = sc.nextInt() - 1;
        System.out.print("Dragao (1-" + treinadores.get(t2).getDragoes().size() + "): ");
        int d2 = sc.nextInt() - 1;
        sc.nextLine();
        
        Dragao dragao1 = treinadores.get(t1).getDragoes().get(d1);
        Dragao dragao2 = treinadores.get(t2).getDragoes().get(d2);
        
        arena.iniciarBatalha(dragao1, dragao2);
    }
}

/*
ESTRUTURA DE PASTAS:
src/
├── Main.java
├── enums/
│   ├── Elemento.java
│   └── StatusDragao.java
├── exceptions/
│   ├── DragaoInvalidoException.java
│   ├── EnergiaInsuficienteException.java
│   └── TreinadorSemEspacoException.java
├── interfaces/
│   └── ICombate.java
├── abstracts/
│   └── Criatura.java
└── models/
    ├── Dragao.java
    ├── DragaoDeFogo.java
    ├── DragaoDeGelo.java
    ├── DragaoDeRaio.java
    ├── Treinador.java
    └── Arena.java

COMPILAR:
cd src
javac Main.java enums/*.java exceptions/*.java interfaces/*.java abstracts/*.java models/*.java

EXECUTAR:
java Main

CONCEITOS APLICADOS:
✅ POO
✅ Métodos
✅ Construtores (padrão e sobrecarregado)
✅ Encapsulamento
✅ Herança (Dragao extends Criatura)
✅ Interface (ICombate)
✅ Relacionamento (Treinador HAS-A Dragao, Arena USA Dragao)
✅ Enum (Elemento, StatusDragao)
✅ Classe Abstrata (Criatura)
✅ Polimorfismo de método (atacar() sobrecarregado)
✅ Polimorfismo de classe (Dragao como Criatura/ICombate)
✅ Exceções personalizadas (3 tipos)
*/