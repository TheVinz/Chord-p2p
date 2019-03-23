package test;

public class TestFingerTableEntry {
    private int start;
    private TestNode testNode;

    public TestFingerTableEntry(){}

    public TestFingerTableEntry(int start, TestNode testNode){
        this.start = start;
        this.testNode = testNode;
    }

    public void setTestNode(TestNode testNode) {
        this.testNode = testNode;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public TestNode getTestNode() {
        return testNode;
    }
}
