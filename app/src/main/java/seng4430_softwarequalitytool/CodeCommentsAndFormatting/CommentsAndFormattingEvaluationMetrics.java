package seng4430_softwarequalitytool.CodeCommentsAndFormatting;

public class CommentsAndFormattingEvaluationMetrics {

    public CommentsAndFormattingEvaluationMetrics() {
    }

    private String ClassName;

    private long NumberCommentsInUnit;

    private long NumberLinesInUnit;

    private long NumberBlockCommentsInUnit;

    private long NumberMethodsInUnit;

    private long LinesTooLong;

    public int getScore() {
        return ((getCommentScore()*2 + getLinesTooLongScore()) / 3);
    }

    // lines too long
    // < 5% perfect
    // < 10% manageable
    // > 10% Bad
    // returns 0-100
    public int getLinesTooLongScore() {
        // devide by 0 is bad and if there is 0 lines then theres nothing wrong
        if (NumberLinesInUnit == 0) {
            return 100;
        }
        float percentTooLong = (((float)LinesTooLong / (float)NumberLinesInUnit) * 100);
        if (percentTooLong <= 5) {
            return 100;
        }
        // the closer to 10 the lower the percent
        if (percentTooLong <= 10) {
            percentTooLong = percentTooLong - 5;
            return (int)(100-((percentTooLong/5)*100));
        }
        // > 10 is 0
        return 0;
    }

    public int getCommentScore() {
        // in line more important as some people dont use block comments for methods and instead use inline
        return (int)((getBlockCommentScore() + (getInlineCommentScore()*2)) / 3);
    }

    // 10% - 30% of the file should be comments
    // high range for perfect score to compensate for lack of knowing if lines are individual comments or on same line
    // 100% score
    private int getInlineCommentScore() {
        // no 0 divide
        if (NumberLinesInUnit == 0) {
            return 100;
        }
        float percentComments = (((float)NumberCommentsInUnit / (float)NumberLinesInUnit) * 100);

        // returns based on percentage lower then 10
        if (percentComments <= 10) {
            return (int)((percentComments / 10) * 100);
        }
        if (percentComments <= 30) {
            return 100;
        }
        // returns based on percentage above 30
        percentComments = percentComments - 30;
        return (int)(100-((percentComments/70)*100));
    }

    // should have a block comment for each method, this returns score from, 0-100 based on count of methods and block comments
    // score given based on 1 per method indicating best, more is bad and less is bad, after double it defaults to 0
    // and 0 returns 0
    private int getBlockCommentScore() {
        // devide by 0 is a sin
        if (NumberMethodsInUnit == 0) {
            if (NumberBlockCommentsInUnit < 2) {
                return 100;
            }
            else {
                return 0;
            }
        }

        if (NumberBlockCommentsInUnit <= NumberMethodsInUnit) { //
            return (int)((NumberBlockCommentsInUnit / NumberMethodsInUnit) * 100);
        }

        if (NumberBlockCommentsInUnit > (NumberMethodsInUnit*2)) { // if more then double theres too many
            return 0;
        }
        // more block comments then methods but not double
        return (int)(100-((NumberBlockCommentsInUnit-NumberMethodsInUnit)/NumberMethodsInUnit));
    }

    public long getLinesTooLong() {
        return LinesTooLong;
    }

    public void setLinesTooLong(long linesTooLong) {
        LinesTooLong = linesTooLong;
    }

    public long getNumberCommentsInUnit() {
        return NumberCommentsInUnit;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public void setNumberCommentsInUnit(long numberCommentsInUnit) {
        NumberCommentsInUnit = numberCommentsInUnit;
    }


    public long getNumberLinesInUnit() {
        return NumberLinesInUnit;
    }

    public void setNumberLinesInUnit(long numberLinesInUnit) {
        NumberLinesInUnit = numberLinesInUnit;
    }

    public long getNumberBlockCommentsInUnit() {
        return NumberBlockCommentsInUnit;
    }

    public void setNumberBlockCommentsInUnit(long numberBlockCommentsInUnit) {
        NumberBlockCommentsInUnit = numberBlockCommentsInUnit;
    }

    public long getNumberMethodsInUnit() {
        return NumberMethodsInUnit;
    }

    public void setNumberMethodsInUnit(long numberMethodsInUnit) {
        NumberMethodsInUnit = numberMethodsInUnit;
    }


}
