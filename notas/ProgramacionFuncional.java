// 2 cosas a resolver:
// - el tipo de dato demiVariable
// - Como referenciar una función.

// - Tipos de datos para funciones
// En Java 1.8, se añade al api de java un nuevo paquete llamado: java.util.function
// Dentro de el se definien lo que llamamos "Interfaces funcionales"
// Son interfaces para tipos de datos que representan funcione/métodos.
// Básicamente hay 4:
// - Consumer<T>        Función que recibe un argumento de tipo T y no devuelve nada (void)
//                      Por ejemplo, cualquier setter
// - Supplier<T>        Función que no recibe argumentos y devuelve un valor de tipo T
//                      Por ejemplo, cualquier getter
// - Function<T, R>     Función que recibe un argumento de tipo T y devuelve un valor de tipo R
//                      Por ejemplo los mapper, funciones que transforman un dato en otro.
// - Predicate<T>       Función que recibe un argumento de tipo T y devuelve un valor booleano
//                      Por ejemplo, las típicas: hasXX, isXXX
// en este paquete, además de estas interfaces funcionales, tenemos 40 combinaciones de ellas:
// - BiFunction<T, U, R>  Función que recibe dos argumentos de tipo T y U y devuelve un valor de tipo R
// - BiConsumer<T, U>     Función que recibe dos argumentos de tipo T y U y no devuelve nada (void)
// - BiPredicate<T, U>    Función que recibe dos argumentos de tipo T y U y devuelve un valor booleano
// - .... y así montón de ellas.
// Cada una de esas interfaces funcionales, define un UNICO método... 
// Mediante ese método, podemos invocar la función a la que paunta la variable.
// El nombre del método cambia de interfaz funcional en interfaz funcional:
// - Consumers: el método se llama accept
// - Suppliers: el método se llama get
// - Functions: el método se llama apply
// - Predicates: el método se llama test


// - Como referenciar una función.
// En Java 1.8, se añaden varios operadores nuevos. Uno de ellos es el operador ::
// Ese operador permite referenciar una función existente. Hay que indicar antes del operador, dónde está definida.
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.List;

public class ProgramacionFuncional {

    public static void saluda(String nombre) {
        System.out.println("Hola, " + nombre);
    }

    public static String generarSaludoFormal(String nombre) {
        return "Estimado " + nombre;
    }

    public static String generarSaludoInformal(String nombre) {
        return "Hola " + nombre;
    }

    public static void imprimirSaludo(String nombre,  Function<String, String> funcionGeneradoraDeSaludos){
        String saludo = funcionGeneradoraDeSaludos.apply(nombre);
        System.out.println(saludo);
    }

    public static void main(String[] args) {
        saluda("Menchu");

        String nombre = "Felipe";
        saluda(nombre);

        Consumer<String> miVariable = ProgramacionFuncional::saluda;
        miVariable.accept("Federico");

        imprimirSaludo("Menchu", ProgramacionFuncional::generarSaludoFormal);
        imprimirSaludo("Felipe", ProgramacionFuncional::generarSaludoInformal);

        // Ejemplo más realista
        // Un bucle!

        // Pre java 1.5 cómo se hacía un bucle:
        List<Integer> numeros = List.of(1,2,3,4,5);
        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(numeros.get(i));
        }
        // Java 1.5 - 1.8. Aparece el concepto de Iterable... y aparecen los for-each
        for (Integer numero : numeros) {
            System.out.println(numero);
        }
        // Desde Java 1.8: Todas las colecciones incorporan una nueva función forEach... basada en programación funcional.
        numeros.forEach(System.out::println); // Esto es lo que se llama un bucle interno.. y es más eficiente computacionalmente que los bucles tradicionales...
        // En cualquier caso.. sería una micro-optimización de cñódigo.. no nos plamnteamos opiner este tipo de bucles para mejorar eficiencia...
        // Lo usamos por comodidad.

        // Cuando empezamos con programacion funcional, muchas veces no creamos funciones apra reusar código... solo las creamos poque nos vemos en la necesidad..
        // Queremos llamar a una función que necesita otras funcioens como argumentos.. pues hay que crearlas! QUE REMEDIO!
        // El problema es que en ocasiones, el definir esas funciones que vamos a pasar como argumentos puede no mejorar la legibilidad del código... sino complicarla!
        imprimirSaludo("Felipe", ProgramacionFuncional::generarSaludoFormal);
        // Qué imprime por pantalla esta linea? Ups... espera.. que no me acuerdo que sacaba la función generarSaludoFormal...
        // Voy a mirarlo.. lo tengo delante ese código? A SCROLLEAR! y eso si es que no esta definida en otro fichero... VAYA TOSTON.
        // El tener esa función fedinida de forma tradicional me ayuda a leer mejor el código / entenderlo mejor.
        
        // Cuando necesito una función, pero tenerla definida en otro sitio no aporta legibilidad, y cuando no quiero reutilizarla.
        // Es decir: CUANDO CREO LA FUNCION PORQUE NO QUEDA MAS REMEDIO!
        // Entonces, en Java 1.8 (y en casi cualquier lenguaje de programación del mundo) tenemos las expresiones lambdas.
        // Qué es una expresión lambda?
        // Una expresión lambda es ante todo: UNA EXPRESION

        String texto ="hola" ; // A este linea de código y similares se las denomina STATEMENTS (declaración, instrucción, sentencia). Sentencia en español = ORACION = FRASE
                               // Esto es una FRASE en JAVA
        int numero = 7;        // Otra sentencia en JAVA
        int numero2 = 5+7;     // Otra sentencia en JAVA
                      ///         Esto es una expresión: Trozo de código que produce un valor.
        // Una expresión lambda es un trozo de código que devuelve un valo, por ser una expresión.
        // Qué devuelve? Una referencia a una función anónima definida dentro de la misma expresión.
        // Las expresiones lambda son una alternativa a la hora de definir funciones.



        Function<String, String> miFuncionDeSaluditos = (String unNombre) ->{
            return "Hey! " + unNombre; // el tipo de dato que devuelve es String... lo veo.. lo infiero del return
        };
        // Java además, permite compactar mucho esta sintaxis
        Function<String, String> miFuncionDeSaluditos2 = (unNombre) ->{ // Puedo quitar el tipo de datos del argumento... Se infiere.. de la variable.
            return "Hey! " + unNombre; // el tipo de dato que devuelve es String... lo veo.. lo infiero del return
        };
        Function<String, String> miFuncionDeSaluditos3 = unNombre ->{ // Puedo quitar los parentesis si solo tengo 1 argumento
            return "Hey! " + unNombre; // el tipo de dato que devuelve es String... lo veo.. lo infiero del return
        };
        Function<String, String> miFuncionDeSaludito4 = unNombre -> "Hey! " + unNombre; // Puedoquitar la llave y el return, si tiene solo una linea.

        // Y en la mayor parte de los casos, las defino dentro del statement donde las voy a usar.
        imprimirSaludo("Menchu", unNombre -> "Hey! " + unNombre);
        // Esto genera un código más legible y conciso.
        // Legible???? Bueno.. siempre y cuando esté familiarizado con las expresiones lambda.... si no, es como si leyera chino.
        // Una vez que me familiarizo con ellas, se vuelven muy útiles y elegantes.
        
    }
    
// - Consumer<T>        Función que recibe un argumento de tipo T y no devuelve nada (void)
//                      Por ejemplo, cualquier setter
// - Supplier<T>        Función que no recibe argumentos y devuelve un valor de tipo T
//                      Por ejemplo, cualquier getter
// - Function<T, R>     Función que recibe un argumento de tipo T y devuelve un valor de tipo R
//                      Por ejemplo los mapper, funciones que transforman un dato en otro.
// - Predicate<T>       Función que recibe un argumento de tipo T y devuelve un valor booleano
//                      Por ejemplo, las típicas: hasXX, isXXX
}
