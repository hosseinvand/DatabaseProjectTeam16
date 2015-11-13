package core;

public class ComputeValue {
	private String expression;

    public ComputeValue(String expression) {
        this.expression = expression;
	}

    public String getValue(Row row) {
        String[] splitedExpression = expression.split("(?<=[-+*/])|(?=[-+*/])");
        String result = "";
        int i = 0;
        if ((int) splitedExpression[0].charAt(0) <= 57 && (int) splitedExpression[0].charAt(0) >= 48) {
            result = splitedExpression[0];
            i++;
        } else if (splitedExpression[0].charAt(0) == '-') {
            result = splitedExpression[0] + splitedExpression[1];
            i = i + 2;
        } else if ((int) splitedExpression[0].charAt(0) == 34) {
            result = splitedExpression[0].replaceAll("\"", "");
            i++;
        } else {
            String value = row.getValue(splitedExpression[0]).toString();
            if (true) {// row.getType==ColumnInfo.Type.INT
                int newValue=0;
                newValue = Integer.parseInt(result)+Integer.parseInt(value);
                result = "" + newValue;
            } else {// row.getType==ComumnInfo.Type.STRING
                result = result + value;
            }
        }

        for (; i < splitedExpression.length; i++) {
            if (splitedExpression[i].charAt(0) == '-') {
                int temp = Integer.parseInt(result) - Integer.parseInt(splitedExpression[i + 1]);
                result = "" + temp;
                i++;
                continue;
            }
            if (splitedExpression[i].charAt(0) == '*') {
                int temp = Integer.parseInt(result) * Integer.parseInt(splitedExpression[i + 1]);
                result = "" + temp;
                i++;
                continue;
            }
            if (splitedExpression[i].charAt(0) == '/') {
                int temp = Integer.parseInt(result) / Integer.parseInt(splitedExpression[i + 1]);
                result = "" + temp;
                i++;
                continue;
            }
            if (splitedExpression[i].charAt(0) == '+') {
                if ((int) splitedExpression[i + 1].charAt(0) <= 57 && (int) splitedExpression[0].charAt(0) >= 48) {
                    int temp = Integer.parseInt(result) / Integer.parseInt(splitedExpression[i + 1]);
                    result = "" + temp;
                    i++;
                    continue;
                } else if ((int) splitedExpression[i + 1].charAt(0) == 34) {
                    result = result + splitedExpression[i + 1];
                    i++;
                    continue;
                } else {
                    String value = row.getValue(splitedExpression[i]).toString();
                    if (true) {// row.getType==ColumnInfo.Type.INT
                        int newValue=0;
                        newValue = Integer.parseInt(result)+Integer.parseInt(value);
                        result = "" + newValue;
                    } else {// row.getType==ComumnInfo.Type.STRING
                        result = result + value;
                    }
                    i++;
                    continue;
                }
            }
        }
        return result;
    }

    public boolean isConstant() {
        boolean insideString = false;
        for(int i = 0 ; i < expression.length(); ++i) {
            char curchar = expression.charAt(i);
            if(curchar == '\"')
                insideString = !insideString;
            if(Character.isLetter(curchar) && !insideString)
                return false;
        }
        return true;
    }
}