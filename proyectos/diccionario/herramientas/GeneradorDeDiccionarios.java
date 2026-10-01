import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Genera los diccionarios de ejemplo de diccionario-es y diccionario-en.
//
//     cd proyectos/diccionario
//     java herramientas/GeneradorDeDiccionarios.java
//
// No tenemos 5000 palabras reales con definiciones y traducción (ni licencia para copiarlas),
// así que se inventan: pseudopalabras, las mismas en los dos idiomas, con acepciones y ejemplos
// escritos en cada idioma. Semilla fija: dos ejecuciones dan exactamente los mismos ficheros,
// y un cambio en el generador se ve como un diff normal.
//
// Delante van unas cuantas palabras reales escritas a mano, para probar con algo con sentido.
public class GeneradorDeDiccionarios {

    static final int PALABRAS = 5000;
    static final long SEMILLA = 20261001L;

    static final String[] CONSONANTES = { "b", "c", "d", "f", "g", "l", "m", "n", "p", "r", "s", "t", "v", "z", "ch", "br", "tr", "pl" };
    static final String[] VOCALES = { "a", "e", "i", "o", "u" };
    static final String[] FINALES = { "", "", "", "n", "s", "r", "l" };

    // Cada par es { español, inglés }: así las dos versiones dicen lo mismo.
    static final String[][] COSAS = {
            { "planta", "plant" }, { "herramienta", "tool" }, { "ave", "bird" }, { "pez", "fish" },
            { "tela", "fabric" }, { "baile", "dance" }, { "instrumento musical", "musical instrument" },
            { "piedra", "stone" }, { "dulce", "sweet" }, { "juego de mesa", "board game" },
            { "insecto", "insect" }, { "barco", "boat" }, { "sombrero", "hat" }, { "queso", "cheese" },
            { "árbol", "tree" }, { "mineral", "mineral" }, { "canción popular", "folk song" }, { "seta", "mushroom" },
    };
    static final String[][] RASGOS = {
            { "propio de las zonas de montaña", "typical of mountain areas" },
            { "de color muy vivo", "with a very bright colour" },
            { "que se usaba antiguamente", "that was used in the old days" },
            { "muy apreciado en la cocina", "highly valued in cooking" },
            { "de origen desconocido", "of unknown origin" },
            { "que solo aparece en invierno", "that only appears in winter" },
            { "típico de la costa", "typical of the coast" },
            { "bastante difícil de encontrar", "rather hard to find" },
    };
    static final String[][] PERSONAS = {
            { "habla demasiado", "talks too much" },
            { "llega siempre tarde", "is always late" },
            { "colecciona cosas inútiles", "collects useless things" },
            { "presume de lo que no tiene", "boasts about what they do not have" },
            { "se ríe de todo", "laughs at everything" },
            { "nunca termina lo que empieza", "never finishes what they start" },
    };
    static final String[][] ACCIONES = {
            { "mezclar dos colores", "mixing two colours" },
            { "doblar papel", "folding paper" },
            { "silbar en voz baja", "whistling softly" },
            { "ordenar la casa", "tidying up the house" },
            { "contar historias", "telling stories" },
    };
    // %s es la palabra.
    static final String[][] EJEMPLOS = {
            { "Ayer vi un %s en el mercado", "Yesterday I saw a %s at the market" },
            { "Mi abuela siempre hablaba del %s", "My grandmother always talked about the %s" },
            { "Nunca había oído la palabra %s", "I had never heard the word %s" },
            { "El %s de mi pueblo es famoso", "The %s from my village is famous" },
            { "No seas %s", "Don't be such a %s" },
    };

    static final List<String> REALES_ES = List.of(
            "melón=Fruto grande, redondo y de pulpa jugosa y dulce.(Me gusta comer melón!)|Persona con pocas luces.(Eres un melón)(No seas melón)",
            "pera=Fruto del peral.",
            "banco=Asiento largo en el que caben varias personas.(Nos sentamos en un banco del parque)|Entidad que guarda dinero y concede préstamos.(He pedido una hipoteca al banco)|Conjunto numeroso de peces que nadan juntos.(Vimos un banco de sardinas)",
            "gato=Mamífero felino doméstico.(El gato duerme en el sofá)|Herramienta para levantar pesos, sobre todo coches.(Saca el gato del maletero para cambiar la rueda)|Persona nacida en Madrid.(Mi abuela es gata de toda la vida)",
            "hoja=Órgano verde y plano de las plantas.(En otoño se caen las hojas)|Lámina de papel.(Dame una hoja para apuntarlo)|Cuchilla de un arma o herramienta.(La hoja del cuchillo está mellada)",
            "sierra=Herramienta con una hoja dentada para cortar.(Corta la tabla con la sierra)|Cordillera de montes.(Pasamos el fin de semana en la sierra)",
            "cabo=Extremo de una cosa.(Ata los dos cabos de la cuerda)|Lengua de tierra que entra en el mar.(Llegamos al cabo de Gata)|Militar de rango inmediatamente superior al soldado.(El cabo pasó revista)");

    static final List<String> REALES_EN = List.of(
            "melon=A large round fruit with sweet juicy flesh.(A slice of melon)",
            "pear=The fruit of the pear tree.",
            "bank=An organisation that keeps money and lends it.(I asked the bank for a mortgage)|The land along the side of a river.(We sat on the river bank)",
            "cat=A small domestic feline.(The cat is sleeping on the sofa)",
            "leaf=A flat green part of a plant.(The leaves fall in autumn)|A sheet of paper in a book.(She turned the leaf)",
            "saw=A tool with a toothed blade for cutting.(Cut the board with the saw)");

    public static void main(String[] args) throws IOException {
        Random azar = new Random(SEMILLA);
        Set<String> reales = new LinkedHashSet<>();
        for (String linea : REALES_ES) reales.add(linea.split("=")[0]);
        for (String linea : REALES_EN) reales.add(linea.split("=")[0]);

        Set<String> palabras = new LinkedHashSet<>();
        while (palabras.size() < PALABRAS) {
            String palabra = inventarPalabra(azar);
            if (!reales.contains(palabra)) {
                palabras.add(palabra);
            }
        }

        List<String> espanol = new ArrayList<>(REALES_ES);
        List<String> ingles = new ArrayList<>(REALES_EN);
        for (String palabra : palabras) {
            String[] linea = acepciones(palabra, azar);
            espanol.add(linea[0]);
            ingles.add(linea[1]);
        }

        escribir(Path.of("diccionario-es/src/main/resources/META-INF/diccionarios/es.txt"), espanol);
        escribir(Path.of("diccionario-en/src/main/resources/META-INF/diccionarios/en.txt"), ingles);
    }

    static String inventarPalabra(Random azar) {
        StringBuilder palabra = new StringBuilder();
        int silabas = 2 + azar.nextInt(3);
        for (int i = 0; i < silabas; i++) {
            palabra.append(CONSONANTES[azar.nextInt(CONSONANTES.length)]).append(VOCALES[azar.nextInt(VOCALES.length)]);
        }
        return palabra.append(FINALES[azar.nextInt(FINALES.length)]).toString();
    }

    // Devuelve { línea en español, línea en inglés }, con las mismas acepciones en el mismo orden.
    static String[] acepciones(String palabra, Random azar) {
        int cuantas = 1 + azar.nextInt(3);
        List<String> es = new ArrayList<>();
        List<String> en = new ArrayList<>();
        for (int i = 0; i < cuantas; i++) {
            String[] texto = switch (azar.nextInt(3)) {
                case 0 -> {
                    String[] cosa = elegir(COSAS, azar);
                    String[] rasgo = elegir(RASGOS, azar);
                    yield new String[] { "Tipo de " + cosa[0] + " " + rasgo[0] + ".", "A kind of " + cosa[1] + " " + rasgo[1] + "." };
                }
                case 1 -> {
                    String[] persona = elegir(PERSONAS, azar);
                    yield new String[] { "Persona que " + persona[0] + ".", "A person who " + persona[1] + "." };
                }
                default -> {
                    String[] accion = elegir(ACCIONES, azar);
                    yield new String[] { "Acción y efecto de " + accion[0] + ".", "The act of " + accion[1] + "." };
                }
            };
            StringBuilder significadoEs = new StringBuilder(texto[0]);
            StringBuilder significadoEn = new StringBuilder(texto[1]);
            int ejemplos = azar.nextInt(3);
            for (int j = 0; j < ejemplos; j++) {
                String[] ejemplo = elegir(EJEMPLOS, azar);
                significadoEs.append('(').append(ejemplo[0].formatted(palabra)).append(')');
                significadoEn.append('(').append(ejemplo[1].formatted(palabra)).append(')');
            }
            es.add(significadoEs.toString());
            en.add(significadoEn.toString());
        }
        return new String[] { palabra + "=" + String.join("|", es), palabra + "=" + String.join("|", en) };
    }

    static String[] elegir(String[][] opciones, Random azar) {
        return opciones[azar.nextInt(opciones.length)];
    }

    static void escribir(Path fichero, List<String> lineas) throws IOException {
        Files.createDirectories(fichero.getParent());
        Files.write(fichero, lineas, StandardCharsets.UTF_8);
        System.out.println(lineas.size() + " palabras en " + fichero);
    }
}
