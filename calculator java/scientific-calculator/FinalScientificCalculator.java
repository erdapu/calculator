import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Math;

public class FinalScientificCalculator extends JFrame implements ActionListener {
    private JTextField displayField;
    private JButton addButton, subButton, mulButton, divButton, clearButton, exitButton, equalsButton;
    private JButton percentButton, sinButton, cosButton, ncrButton, factorialButton, acosButton, secButton, cotButton, nprButton;
    private JButton sqrtButton, squareButton, cubeButton, powerButton, inverseButton;
    private JButton eButton, expButton, logButton, lnButton;
    private JButton piButton, nPiButton, power2Button, power10Button;
    private JButton[] digitButtons = new JButton[10];
    private JButton decimalButton;
    private JButton absButton;

    private JPanel panel;

    // --- State Variables for Calculation Deferral ---
    private double lastValue = 0;       // Stores the first number of the current expression chain.
    private String pendingOperator = ""; // Stores the binary operator (+, -, etc.) awaiting the second operand.
    private String pendingUnaryOp = "";  // Stores the function name (sin, sqrt, etc.) awaiting execution.
    private boolean isAwaitingNewInput = true; // True if the next digit should clear the screen.
    private boolean displayNeedsClear = true; // True if display holds the function name/previous result.
    // ------------------------------------------------

    private final String ERROR_MESSAGE = "Error"; 

    // Define a palette for attractive UI
    private final Color BACKGROUND_COLOR = new Color(50, 50, 50); 
    private final Color DISPLAY_BACKGROUND_COLOR = new Color(200, 200, 200); 
    private final Color DISPLAY_FOREGROUND_COLOR = Color.BLACK;
    private final Color DIGIT_BUTTON_BACKGROUND = new Color(80, 80, 80); 
    private final Color OPERATOR_BUTTON_BACKGROUND = new Color(255, 165, 0); 
    private final Color FUNCTION_BUTTON_BACKGROUND = new Color(65, 105, 225); 
    private final Color CLEAR_EXIT_BUTTON_BACKGROUND = new Color(220, 20, 60); 
    private final Color EQUALS_BUTTON_BACKGROUND = new Color(0, 128, 0); 
    private final Color TEXT_COLOR = Color.WHITE;

    public FinalScientificCalculator() {
        setTitle("Scientific Calculator");
        setSize(600, 750);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // --- UI Setup (Unchanged) ---
        JLabel titleLabel = new JLabel("Scientific Calculator", JLabel.CENTER);
        titleLabel.setBounds(50, 10, 500, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(TEXT_COLOR);
        add(titleLabel);

        displayField = new JTextField("0");
        displayField.setBounds(50, 70, 500, 70);
        displayField.setFont(new Font("Segoe UI Light", Font.BOLD, 36));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(DISPLAY_BACKGROUND_COLOR);
        displayField.setForeground(DISPLAY_FOREGROUND_COLOR);
        displayField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(displayField);

        for (int i = 0; i < 10; i++) {
            digitButtons[i] = createButton(String.valueOf(i), DIGIT_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        }
        decimalButton = createButton(".", DIGIT_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));

        addButton = createButton("+", OPERATOR_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        subButton = createButton("-", OPERATOR_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        mulButton = createButton("*", OPERATOR_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        divButton = createButton("/", OPERATOR_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        equalsButton = createButton("=", EQUALS_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));

        clearButton = createButton("C", CLEAR_EXIT_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 20));
        exitButton = createButton("Exit", CLEAR_EXIT_BUTTON_BACKGROUND.darker(), TEXT_COLOR, new Font("Segoe UI", Font.BOLD, 16)); 

        percentButton = createButton("%", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        sinButton = createButton("sin", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        cosButton = createButton("cos", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        ncrButton = createButton("nCr", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        factorialButton = createButton("n!", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        acosButton = createButton("cos^-1", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        secButton = createButton("sec", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        cotButton = createButton("cot", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        nprButton = createButton("nPr", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        sqrtButton = createButton("sqrt", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 18)); 
        squareButton = createButton("x^2", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        cubeButton = createButton("x^3", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        powerButton = createButton("x^y", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        inverseButton = createButton("x^-1", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        eButton = createButton("e", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        expButton = createButton("e^x", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        logButton = createButton("log", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        lnButton = createButton("ln", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        absButton = createButton("|x|", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        piButton = createButton("pi", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        nPiButton = createButton("n*pi", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16));
        power2Button = createButton("2^x", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 
        power10Button = createButton("10^x", FUNCTION_BUTTON_BACKGROUND, TEXT_COLOR, new Font("Segoe UI", Font.PLAIN, 16)); 

        panel = new JPanel();
        panel.setBounds(50, 160, 500, 550);
        panel.setLayout(new GridLayout(8, 5, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        add(panel);

        addButtonsToPanel();
        setVisible(true);
    }

    private JButton createButton(String text, Color background, Color foreground) {
        return createButton(text, background, foreground, new Font("Segoe UI", Font.BOLD, 16));
    }
    
    private JButton createButton(String text, Color background, Color foreground, Font font) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BACKGROUND_COLOR.darker(), 1));
        button.addActionListener(this);
        return button;
    }

    private void addButtonsToPanel() {
        panel.add(eButton);
        panel.add(piButton);
        panel.add(clearButton);
        panel.add(exitButton);
        panel.add(divButton);
        panel.add(sinButton);
        panel.add(cosButton);
        panel.add(logButton);
        panel.add(lnButton);
        panel.add(mulButton);
        panel.add(acosButton);
        panel.add(secButton);
        panel.add(cotButton);
        panel.add(factorialButton);
        panel.add(subButton);
        panel.add(sqrtButton);
        panel.add(squareButton);
        panel.add(powerButton);
        panel.add(ncrButton);
        panel.add(addButton);
        panel.add(expButton);
        panel.add(power2Button);
        panel.add(power10Button);
        panel.add(nprButton);
        panel.add(inverseButton);
        panel.add(digitButtons[7]);
        panel.add(digitButtons[8]);
        panel.add(digitButtons[9]);
        panel.add(absButton);
        panel.add(equalsButton);
        panel.add(digitButtons[4]);
        panel.add(digitButtons[5]);
        panel.add(digitButtons[6]);
        panel.add(percentButton);
        panel.add(nPiButton);
        panel.add(digitButtons[1]);
        panel.add(digitButtons[2]);
        panel.add(digitButtons[3]);
        panel.add(digitButtons[0]);
        panel.add(decimalButton);
    }

    // Executes the stored unary operation on the given number
    private double executePendingUnary(double num) throws ArithmeticException {
        String op = pendingUnaryOp;
        pendingUnaryOp = ""; // Clear the buffer immediately
        
        switch (op) {
            case "sin": return Math.sin(Math.toRadians(num));
            case "cos": return Math.cos(Math.toRadians(num));
            case "sec": {
                double cosVal = Math.cos(Math.toRadians(num));
                if (Math.abs(cosVal) < 1e-10) throw new ArithmeticException("Sec undefined");
                return 1.0 / cosVal;
            }
            case "cot": {
                double sinVal = Math.sin(Math.toRadians(num));
                if (Math.abs(sinVal) < 1e-10) throw new ArithmeticException("Cot undefined");
                return 1.0 / Math.tan(Math.toRadians(num));
            }
            case "cos^-1": {
                if (num < -1 || num > 1) throw new ArithmeticException("Domain [-1, 1]");
                return Math.toDegrees(Math.acos(num));
            }
            case "sqrt": {
                if (num < 0) throw new ArithmeticException("Negative root");
                return Math.sqrt(num);
            }
            case "x^2": return num * num;
            case "x^3": return num * num * num;
            case "x^-1": {
                if (num == 0) throw new ArithmeticException("Division by Zero");
                return 1.0 / num;
            }
            case "e^x": return Math.exp(num);
            case "log": {
                if (num <= 0) throw new ArithmeticException("Log non-positive");
                return Math.log10(num);
            }
            case "ln": {
                if (num <= 0) throw new ArithmeticException("Ln non-positive");
                return Math.log(num);
            }
            case "|x|": return Math.abs(num);
            case "n*pi": return num * Math.PI;
            case "2^x": return Math.pow(2, num);
            case "10^x": return Math.pow(10, num);
            case "n!": {
                if (num < 0 || num > 20 || num != (int) num) throw new ArithmeticException("n! (0-20)");
                return factorial((int) num);
            }
            case "%": return num / 100.0;
            default: return num;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        JButton source = (JButton) e.getSource();

        // 1. Digit/Decimal Input
        if (command.matches("[0-9]") || command.equals(".")) {
            if (isAwaitingNewInput || displayField.getText().equals("0") || displayField.getText().startsWith(ERROR_MESSAGE)) {
                // If a binary op is pending, we might be starting the second number
                if (displayNeedsClear && !pendingOperator.isEmpty()) {
                    displayField.setText("");
                } else if (displayNeedsClear || displayField.getText().startsWith(ERROR_MESSAGE) || displayField.getText().equals("0")) {
                    displayField.setText("");
                }
                
                if (command.equals(".")) {
                    if (!displayField.getText().contains(".")) displayField.setText(displayField.getText().isEmpty() ? "0." : displayField.getText() + ".");
                } else {
                    displayField.setText(displayField.getText() + command);
                }
                isAwaitingNewInput = false;
                displayNeedsClear = false;
                pendingUnaryOp = ""; // Clear any pending unary op if user starts typing
            } else if (command.equals(".")) {
                if (!displayField.getText().contains(".")) {
                    displayField.setText(displayField.getText() + ".");
                }
            } else {
                displayField.setText(displayField.getText() + command);
            }
            return;
        }

        // 2. Clear / Exit
        if (source == clearButton) {
            displayField.setText("0");
            lastValue = 0;
            pendingOperator = "";
            pendingUnaryOp = "";
            isAwaitingNewInput = true;
            displayNeedsClear = true;
            return;
        }

        if (source == exitButton) {
            System.exit(0);
        }

        try {
            final double num = parseDisplayValue();
            
            // --- 3. Constant Buttons ---
            if (source == eButton || source == piButton) { 
                double result = (source == eButton) ? Math.E : Math.PI;
                displayField.setText(formatResult(result));
                isAwaitingNewInput = true;
                displayNeedsClear = true;
                return;
            }

            // --- 4. Unary Functions (Display Operation, Defer Calculation) ---
            if (source == sinButton || source == cosButton || source == secButton || source == cotButton || 
                source == acosButton || source == sqrtButton || source == squareButton || source == cubeButton || 
                source == inverseButton || source == expButton || source == logButton || source == lnButton || 
                source == absButton || source == nPiButton || source == power2Button || source == power10Button || 
                source == factorialButton || source == percentButton) 
            {
                String currentDisplayNum = getDisplayNumberForFunction();

                // Store the current operation and display the expression
                displayField.setText(command + "(" + currentDisplayNum + ")");
                pendingUnaryOp = command; // Store the function name
                isAwaitingNewInput = false;
                displayNeedsClear = false; // Don't clear input if user types another number for the next step
                return;
            }
            
            // --- 5. Binary Operator Buttons (Display Operation, Defer Calculation) ---
            if (source == addButton || source == subButton || source == mulButton || source == divButton || 
                source == powerButton || source == ncrButton || source == nprButton) 
            {
                double finalNum = num;
                
                // If a unary op was pending (e.g., sin(90)), execute it first.
                if (!pendingUnaryOp.isEmpty()) {
                    finalNum = executePendingUnary(num); 
                }

                if (!pendingOperator.isEmpty()) {
                    // Chain the operation: Calculate the *previous* pending operation
                    lastValue = calculate(lastValue, finalNum, pendingOperator);
                } else {
                    lastValue = finalNum; // This is the start of the chain
                }

                // Update the state for the *new* binary operation
                pendingOperator = command;
                displayField.setText(formatResult(lastValue) + " " + pendingOperator); // Display the first number and the operator
                isAwaitingNewInput = true; // Next digit clears the screen
                displayNeedsClear = true;
                pendingUnaryOp = "";
                return;
            }

            // --- 6. Equals Button: FINAL CALCULATION ---
            else if (source == equalsButton) {
                
                double finalNum = num;
                
                // Execute pending unary operation on the current number first
                if (!pendingUnaryOp.isEmpty()) {
                    finalNum = executePendingUnary(num);
                }

                double finalResult;
                if (!pendingOperator.isEmpty()) {
                    // Execute the final pending binary operation
                    finalResult = calculate(lastValue, finalNum, pendingOperator);
                } else {
                    // If no binary operator, just execute the unary result (which is stored in finalNum)
                    finalResult = finalNum;
                }

                // Reset all state variables
                displayField.setText(formatResult(finalResult));
                lastValue = finalResult;
                pendingOperator = "";
                pendingUnaryOp = "";
                isAwaitingNewInput = true;
                displayNeedsClear = true;
                return;
            }


        } catch (NumberFormatException nfe) {
            displayField.setText(ERROR_MESSAGE);
            lastValue = 0; pendingOperator = ""; pendingUnaryOp = ""; isAwaitingNewInput = true; displayNeedsClear = true;
        } catch (ArithmeticException ae) {
            displayField.setText("Error: " + ae.getMessage());
            lastValue = 0; pendingOperator = ""; pendingUnaryOp = ""; isAwaitingNewInput = true; displayNeedsClear = true;
        } catch (Exception ex) {
            displayField.setText(ERROR_MESSAGE);
            lastValue = 0; pendingOperator = ""; pendingUnaryOp = ""; isAwaitingNewInput = true; displayNeedsClear = true;
        }
    }
    
    // Helper to get the number part from the display (clears functions for clean display)
    private String getDisplayNumberForFunction() {
        String text = displayField.getText();
        if (text.contains("(")) {
            // If sin(90) is on screen, use the last calculated number (lastValue)
            return formatResult(lastValue);
        } else if (text.contains(" ")) {
            // If 90 + is on screen, use the last value
            return formatResult(lastValue);
        }
        return text;
    }

    private double parseDisplayValue() throws NumberFormatException {
        String text = displayField.getText();
        if (text.startsWith("Error")) {
            return 0;
        }
        // If the display shows "sin(90)" or "90 +", we can't parse it directly as the second number.
        // We rely on the logic in actionPerformed to handle this state.
        if (text.contains("(") || text.contains(" ")) {
             // For the calculation path, if the display shows a pending op, we must be referring to the value *before* the op.
             if (!pendingOperator.isEmpty()) {
                 // Return lastValue if we are mid-chain (e.g. 50 + sin(90))
                 return lastValue;
             }
             // For unary ops, try to extract the number inside the parentheses.
             int lastParen = text.lastIndexOf('(');
             if (lastParen != -1) {
                 text = text.substring(lastParen + 1, text.indexOf(')'));
             } else {
                 // For safety, return 0 if the display is non-numeric (e.g. '+')
                 return 0;
             }
        }
        return Double.parseDouble(text);
    }

    private String formatResult(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.valueOf(value);
        }
    }

    private double calculate(double n1, double n2, String op) throws ArithmeticException {
        switch (op) {
            case "+": return n1 + n2;
            case "-": return n1 - n2;
            case "*": return n1 * n2;
            case "/":
                if (n2 == 0) throw new ArithmeticException("Division by Zero");
                return n1 / n2;
            case "x^y": return Math.pow(n1, n2);
            case "nCr":
                if (n1 < 0 || n2 < 0 || n2 > n1) throw new ArithmeticException("nCr/nPr domain");
                return combination((int) n1, (int) n2);
            case "nPr":
                if (n1 < 0 || n2 < 0 || n2 > n1) throw new ArithmeticException("nCr/nPr domain");
                return permutation((int) n1, (int) n2);
            default: return n2;
        }
    }

    private double factorial(int n) {
        if (n < 0) return Double.NaN;
        long fact = 1;
        for (int i = 2; i <= n; i++) {
            if (fact > Long.MAX_VALUE / i) { 
                throw new ArithmeticException("Factorial overflow");
            }
            fact *= i;
        }
        return (double) fact;
    }

    private double combination(int n, int r) {
        if (r < 0 || r > n) return 0;
        if (r == 0 || r == n) return 1;
        if (r > n / 2) r = n - r; 
        
        double res = 1;
        for (int i = 1; i <= r; i++) {
            res = res * (n - i + 1) / i;
        }
        return res;
    }

    private double permutation(int n, int r) {
        if (r < 0 || r > n) return 0;
        double res = 1;
        for (int i = 0; i < r; i++) {
            res *= (n - i);
        }
        return res;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinalScientificCalculator());
    }
}