package core;

public class ComputeValue {
    private String expression;

    public ComputeValue(String expression) {
        this.expression = expression;
        this.expression += "+";
    }

    public String getValue(Row row) {
        String token = "";
        String operator = "-+*/";
        String result = "";
        boolean stringSeen = false;
        boolean insideString = false;
        char lastOperator = 0;
        expression.trim();
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);

            if (insideString) {
                token += c;
                if (c != '\"') {
                    continue;
                } else {
                    insideString = false;
                    continue;
                }
            }

            if (c == ' ')
                continue;

            if (operator.indexOf(c) != -1) {
                if (token.contains("\"")) { // "xxx"
                    result += token.substring(1, token.length() - 1);
                } else {
                    if (!numeric(token) && row.getType(token).equals(ColumnInfo.Type.INT)) // convert INT VAR to value
                        token = row.getValue(token);
                    if (numeric(token)) { // number
                        if (stringSeen)
                            result += token;
                        else {
                            switch (lastOperator) {
                                case '+':
                                    result = Integer.toString(Integer.valueOf(result) + Integer.valueOf(token));
                                    break;
                                case '-':
                                    result = Integer.toString(Integer.valueOf(result) - Integer.valueOf(token));
                                    break;
                                case '*':
                                    result = Integer.toString(Integer.valueOf(result) * Integer.valueOf(token));
                                    break;
                                case '/':
                                    result = Integer.toString(Integer.valueOf(result) / Integer.valueOf(token));
                                    break;
                                case 0:
                                    result = token;
                                    break;
                            }
                        }
                    } else { // VAR
                        token = row.getValue(token);
                        stringSeen = true;
                        result += token;
                    }
                }

                lastOperator = c;
                token = "";
                continue;
            }

            if (c == '\"') {
                token += c;
                insideString = true;
                stringSeen = true;
                continue;
            }

            token += c;
        }

        return result;
    }

    private boolean numeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    public boolean isConstant() {
        boolean insideString = false;
        for (int i = 0; i < expression.length(); ++i) {
            char curchar = expression.charAt(i);
            if (curchar == '\"')
                insideString = !insideString;
            if (Character.isLetter(curchar) && !insideString)
                return false;
        }
        return true;
    }
}