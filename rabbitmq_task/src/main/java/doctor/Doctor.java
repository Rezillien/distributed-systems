package doctor;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConstValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Doctor {

    private Channel commonChannel;
    private String id;

    public Doctor() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();

        this.commonChannel = connection.createChannel();
        commonChannel.exchangeDeclare(ConstValues.EXCHANGE_NAME_COMMON, BuiltinExchangeType.TOPIC);

        getInformation();
    }

    private void getInformation() throws IOException {
        System.out.println("Doctor connected. Input ID:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        id = bufferedReader.readLine();
    }

    private void initWorkers() throws IOException {
        DoctorMessageEmitter emitter = new DoctorMessageEmitter(commonChannel, id);
        DoctorMessageListener listener = new DoctorMessageListener(commonChannel, id);
        emitter.startTask();
        listener.receiveMessage();
    }

    public static void main(String[] args) throws Exception{
        Doctor doctor = new Doctor();
        doctor.initWorkers();
    }
}
