import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class StubGen {
    public static void generateur(String s) {
        // on retire le "_itf" du nom de la classe
        String classeImpl = s.split("_")[0];
        String classeStub = (classeImpl + "_stub");

        try {
            // on récupère la classe correspondant au nom en entrée
            Class<?> classe = Class.forName(s);
            // PrintWriter pour écrire dans le fichier
            PrintWriter printer = new PrintWriter(classeStub + ".java");
            // Ecriture de la classe
            printer.format("public class " + classeStub + " extends SharedObject implements " + classeImpl + "_itf , java.io.Serializable { \n");
            printer.flush();
            // Ecriture du constructeur
            printer.format("\n\tpublic " + classeStub + "(Object o, int ident) {\n\t\tsuper(o, ident);} \n");
            printer.flush();
            // Recuperation des methodes de la classe
            Method[] methodes = classe.getDeclaredMethods();
            // On ecrit chaque signature des methodes de la classe initiale et on appelle les methodes initiales
            for(Method m : methodes) {
                if(Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
                    // types des retours et parametres en entrée
                    String typeRetour = m.getReturnType().getSimpleName();
                    Class<?>[] typeParametres = m.getParameterTypes();
                    // chaine de caracteres qui va contenir les arguments dans la signature
                    String arguments = "";
                    // chaine de caractere qui va contenir les parametres a passer dans la methode d'origine
                    String parametres = "";
                    // on fabrique ces chaines
                    for (int i = 0; i<typeParametres.length; i++) {
                        arguments += typeParametres[i].getSimpleName() + " " + "arg" + i + ", ";
                        parametres += "arg" + i + ", ";
                    }
                    // on enleve les caracteres inutiles
                    if(arguments.length() > 0) {
                        arguments = arguments.substring(0,arguments.length() - 2);
                        parametres = parametres.substring(0, parametres.length() - 2);
                    }
                    // on ecrit la signature de la methode et on cast l'objet contenu dans le SharedObject avec la classe initiale
                    printer.format("\n\tpublic " + typeRetour + " " + m.getName() + "(" + arguments + ") {");
                    printer.flush();
                    if (m.getAnnotation(Read.class) != null) {
                        // annotation read
                        printer.format("\n\t\tsuper.lock_read();");
                        printer.flush();   
                    } else if(m.getAnnotation(Write.class) != null) {
                        // annotation write
                        printer.format("\n\t\tsuper.lock_write();");
                        printer.flush();
                    }
                    printer.format("\n\t\t"+ classeImpl + " objet = (" + classeImpl + ") obj;");
                    printer.flush();

                    // si on ne retourne rien on execute juste la methode initiale sinon on la return
                    if (typeRetour.equals("void")) {
                        printer.format("\n\t\tobjet."+ m.getName() + "(" + parametres + ");");
                        printer.flush();
                        // unlock
                        if ((m.getAnnotation(Read.class) != null) || (m.getAnnotation(Write.class) != null)) {
                            printer.format("\n\t\tsuper.unlock();");
                            printer.flush();
                        }
                    } else {
                        printer.format("\n\t\t" + typeRetour + " ret = objet."+ m.getName() + "(" + parametres + ");");
                        printer.flush();
                        // unlock
                        if ((m.getAnnotation(Read.class) != null) || (m.getAnnotation(Write.class) != null)) {
                            printer.format("\n\t\tsuper.unlock();");
                            printer.flush();
                        }
                        printer.format("\n\t\treturn ret;");
                        printer.flush();
                    }
                    // fermeture du corps de la methode
                    printer.format("\n\t}");
                    printer.flush();
                }
            }
            // fermeture du corps de la classe
            printer.format("\n}");
            printer.flush();
            printer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //COMPILATEUR -> permet de générer dynamiquement les stubs
    public static void compilateur(String s) {

        // Premiere methode de compilation : utilisation du compilateur javac
        /*
        String[] cmd = { "javac " + s + "_stub.java"};
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */
        
        // Deuxieme methode de compilation : utilisation du package javax.tools.JavaCompiler
        
        // Obtenir une instance de l'outil de compilation
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // Créer un gestionnaire de fichiers pour la compilation
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        // Spécifier les fichiers à compiler
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File("./" + s +"_stub.java")));
        // Créer une tâche de compilation pour la classe
        CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        // Exécuter la tâche de compilation
        //boolean success = 
        task.call();

        /*if (success) {
            System.out.println("La classe a été compilée avec succès");
        } else {
            System.out.println("La classe n'a pas pu être compilée");
        } */

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // main utilise pour les tests C:\Program Files (x86)\Common Files\Oracle\Java\javapath
    public static void main(String args[]) {
        StubGen.generateur(args[0]);
        //StubGen.compilateur(args[0]);
    }
}
