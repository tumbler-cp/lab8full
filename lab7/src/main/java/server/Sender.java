package server;

class Sender extends Thread {
    private Complect object;
    private ServerNetwork network;

    @Override
    public void run() {
        System.out.println("Начало отправки в " + object.src());
        network.send(object.alloc().bytes(), object.src());
        network.send(object.data().bytes(), object.src());
        System.out.println("Успешно отправлен ответ: " + object.src());
        System.out.println("Конец отправки!");
    }

    public void setNetwork(ServerNetwork network) {
        this.network = network;
    }

    public void setObject(Complect object) {
        this.object = object;
    }
}
