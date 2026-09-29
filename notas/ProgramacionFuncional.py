
def saluda(nombre):                 # Programación procedural
    print("Hola, " + nombre)

saluda("Menchu")

nombre = "Felipe"   # Declaro una variable que apunta a un texto "Felipe"
saluda(nombre)      # Estoy ejecutando la función saluda? SI
                    # En python, igual que en java, para ejecuatr una función,
                    #  ponemos detrás los paréntesis.
                    # Si necesita argumentos, los pasamos.

# PROGRAMACION FUNCIONAL.
miVariable = saluda # Declaro una variable que apunta a una función: saluda
                    # Estoy ejecutando la función saluda? NO
                    # Solo referencio la función desde una variable.
                    # La variable apunta a la función
                    # Hemos asignado la variable a la función "saluda"

miVariable("Federico") # Ejecuto la función saluda, desde la variable.

def doble(numero):  # Cuando creamos funciones, podemos definirles argumentos
                    # Para qué sirven los argumentos?
                    # - Suministrar datos a la función en tiempo de ejecución.
    return numero * 2

def multiplicar(numero, multiplicador):
    return numero * multiplicador

resultado = doble(5)
print("El resultado es: " + str(resultado))

resultado = multiplicar(5, 3)
print("El resultado es: " + str(resultado))

# Con los argumentos, tradicionalmente he sido capaz de inyectar DATOS en tiempo de ejecución
# a mis funciones...
# Y si quiero inyectar LOGICA?
# Es decir, lo que quiero es inyectar (suministrar coo argumento, en tiempo de ejecución el *)

def imprimir_resultado_de_operar(numero1, numero2, operacion):
    resultado = operacion(numero1, numero2)
    print(resultado)
    # Según la sintaxis que he usado, operación es una función, un método
    # Que recibe cuantos argumentos? 2
    # Y devuelve algo?

def multiplicar (numero1, numero2):
   return numero1*numero2

def sumar(numero1, numero2):
    return numero1 + numero2

def dividir(numero1, numero2):
    return numero1 / numero2

imprimir_resultado_de_operar(5, 3, multiplicar)
imprimir_resultado_de_operar(5, 3, dividir)
imprimir_resultado_de_operar(5, 3, sumar)


def generar_saludo_formal(nombre):
    return "Hola, " + nombre + ". Es un placer saludarle."

def generar_saludo_informal(nombre):
    return "Hola, " + nombre + ". ¿Qué tal?"

def imprimir_saludo(nombre, funcion_generadora_de_saludos):
    saludo = funcion_generadora_de_saludos(nombre)
    print(saludo)

imprimir_saludo("Menchu", generar_saludo_formal)
imprimir_saludo("Felipe", generar_saludo_informal)

# La responsabilidad de imprimir_saludo es:
# - Generar un saludo con una lógica que se inyecta en tiempo de ejecución, y pùede variar de imrpesión en impresión
# - Imprimir el saludo generado.

# Pregunta. Que se ejecuta primero: 
# - imprimir_saludo <<< ESTE SE EJECUTA PRIMERO
# - generar_saludo_formal

# Distinto sería si hubieramos escrito:
# imprimir_saludo(generar_saludo_formal("Menchu")) # En este caso, generar_saludo_formal se ejecuta primero, y su resultado se pasa a imprimir_saludo.
# En este caso, que se habría ejecutado primero? generar_saludo_formal

# Esto importa en muchos escenarios.. y veremos muchos.

# De momento nos quedamos con una cosa....
# Gracias a la programación funcional, puedo inyectar LOGICA en tiempo de ejecución a mis funciones.
# Y en muchos casos, eso cambia radicalmente la forma de escribir código.