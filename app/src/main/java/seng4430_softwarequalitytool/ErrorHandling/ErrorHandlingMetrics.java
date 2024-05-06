package seng4430_softwarequalitytool.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandlingMetrics {
    private int tryCatchCount;
    private int throwCount;
    private int genericExceptionsCount;
    private List<String> exceptionTypes;
    private List<String> unequeExceptionTypes;

    private String className;


    public ErrorHandlingMetrics(int tryCatchCount, int throwCount, List<String> exceptionTypes) {
        this.tryCatchCount = tryCatchCount;
        this.throwCount = throwCount;
        this.exceptionTypes = exceptionTypes;
        this.genericExceptionsCount = 0;

        this.unequeExceptionTypes = new ArrayList<>();

        // calculate unique
        for (String exceptionType : exceptionTypes) {
            if (!unequeExceptionTypes.contains(exceptionType)) {
                unequeExceptionTypes.add(exceptionType);
            }
        }

        // calculate generic
        for (String exceptionType : exceptionTypes) {
            if (exceptionType.equals("Exception") || exceptionType.equals("Throwable")) {
                this.genericExceptionsCount++;
            }
        }

    }

    public int getScore() {
        int score = 0;

        // +35 for try catch
        // +35 for throw
        // +15 uneque exception types
        // +15 for no generic acception types
        // overall somewhere from 0-100

        if (tryCatchCount > 3) {
            score += 35;
        } else if (tryCatchCount > 2) {
            score += 30;
        } else if (tryCatchCount > 1) {
            score += 25;
        } else if (tryCatchCount == 1) {
            score += 15;
        }

        if (throwCount > 3) {
            score += 35;
        } else if (throwCount > 2) {
            score += 30;
        } else if (throwCount > 1) {
            score += 25;
        } else if (throwCount == 1) {
            score += 15;
        }

        if (genericExceptionsCount == 0) {
            score += 15;
        } else if (genericExceptionsCount == 1) {
            score += 10;
        } else if (genericExceptionsCount == 2) {
            score += 5;
        }

        if (unequeExceptionTypes.size() > 1) {
            score += 15;
        } else if (unequeExceptionTypes.size() == 1) {
            score += 10;
        }

        return score;
    }

    public int getGenericExceptionsCount() {
        return genericExceptionsCount;
    }

    public void setGenericExceptionsCount(int genericExceptionsCount) {
        this.genericExceptionsCount = genericExceptionsCount;
    }

    public List<String> getUnequeExceptionTypes() {
        return unequeExceptionTypes;
    }

    public void setUnequeExceptionTypes(List<String> unequeExceptionTypes) {
        this.unequeExceptionTypes = unequeExceptionTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getTryCatchCount() {
        return tryCatchCount;
    }

    public void setTryCatchCount(int tryCatchCount) {
        this.tryCatchCount = tryCatchCount;
    }

    public int getThrowCount() {
        return throwCount;
    }

    public void setThrowCount(int throwCount) {
        this.throwCount = throwCount;
    }

    public List<String> getExceptionTypes() {
        return exceptionTypes;
    }

    public void setExceptionTypes(List<String> exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }
}
