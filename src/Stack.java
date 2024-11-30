import java.util.LinkedList;
import java.util.Random;

class Stack {
    private LinkedList<Integer> stackList = new LinkedList<>();
    private final int LIMIT = 10;

    // Push method to add an element to the stack
    public synchronized void push(int value) throws InterruptedException {
        while (stackList.size() == LIMIT) {
            wait(); // Wait if the stack is full
        }
        stackList.add(value);
        System.out.println("Produced: " + value);
        notifyAll(); // Notify consumers that they can consume
    }

    // Pop method to remove an element from the stack
    public synchronized int pop() throws InterruptedException {
        while (stackList.isEmpty()) {
            wait(); // Wait if the stack is empty
        }
        int value = stackList.removeLast();
        System.out.println("Consumed: " + value);
        notifyAll(); // Notify producers that they can produce
        return value;
    }
}

class Producer extends Thread {
    private Stack stack;
    private Random random = new Random();

    public Producer(Stack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                int value = random.nextInt(100) + 1; // Produce a random integer between 1 and 100
                stack.push(value); // Push the produced value onto the stack
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Producer interrupted");
            }
        }
    }
}

class Consumer extends Thread {
    private Stack stack;

    public Consumer(Stack stack) {
        this.stack = stack;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                stack.pop(); // Pop an integer from the stack
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Consumer interrupted");
            }
        }
    }
}