package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0";

    private double latestValue;

    private String latestOperation = "";

    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
     * drücken kann muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
     * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
     * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
     * @param digit Die Ziffer, deren Taste gedrückt wurde
     */
    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException();

        if(screen.equals("0") || latestValue == Double.parseDouble(screen)) screen = "";

        screen = screen + digit;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * (Erweiterung) Wenn bereits eine Operation ausgeführt wurde und eine Zahl auf dem Bildschirm angezeigt wird,
     * wird die vorherige Operation sofort ausgewertet, bevor die neue Operation gesetzt wird.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt des aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation)  {
        if(!latestOperation.isEmpty() && !screen.equals("0")){      //Bug fix
            pressEqualsKey();

        }

        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
    }

    /**
     * Führt eine unäre Operation auf den aktuellen Bildschirmwert aus: Quadratwurzel, Prozent oder Inversion.
     *
     * - "√": Berechnet die Quadratwurzel des aktuellen Wertes.
     * - "%": Berechnet den Prozentwert basierend auf dem Kontext:
     *   - Ohne vorherige Operation: interpretiert den aktuellen Wert als Prozent (z.B., 10% = 0,1).
     *   - Mit vorheriger Operation: berechnet den Prozentwert relativ zum vorherigen Wert (z.B., "50 + 10%" ergibt 55).
     * - "1/x": Berechnet den Kehrwert des aktuellen Wertes.
     *
     * Aktualisiert den Bildschirm direkt mit dem Ergebnis.
     *
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */

    public void pressUnaryOperationKey(String operation) {
        double result;
        double currentScreenValue = Double.parseDouble(screen); // Get the current screen value

        switch (operation) {
            case "√":
                result = Math.sqrt(currentScreenValue);
                break;
            case "%":
                // Check if there's a previous operation, calculate percentage accordingly
                if (latestOperation.isEmpty()) {
                    // No previous operation, so calculate percentage of current screen value
                    result = currentScreenValue / 100;
                } else {
                    // Calculate percentage based on latestValue (for example, 50 + 10% as 50 + (50 * 10 / 100))
                    result = latestValue * (currentScreenValue / 100);
                }
                break;
            case "1/x":
                result = 1 / currentScreenValue;
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }

        // Update the screen with the result
        screen = Double.toString(result);
        if (screen.equals("NaN") || screen.equals("Infinity")) screen = "Error"; // Handle error cases
        if (screen.endsWith(".0")) screen = screen.substring(0, screen.length() - 2); // Remove unnecessary ".0"
        if (screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10); // Limit to 10 characters if decimal
    }


    /**
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise "."
     * Fügt beim ersten Mal Drücken dem aktuellen Bildschirminhalt das Trennzeichen auf der rechten
     * Seite hinzu und aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts vom
     * Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweimaligem Drücken, oder wenn bereits ein Trennzeichen angezeigt wird, passiert nichts.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }

    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, passiert nichts.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    public void pressEqualsKey() {
        var result = switch(latestOperation) {
            case "+" -> latestValue + Double.parseDouble(screen);
            case "-" -> latestValue - Double.parseDouble(screen);
            case "x" -> latestValue * Double.parseDouble(screen);
            case "/" -> latestValue / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("Infinity")) screen = "Error";
        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }
}
