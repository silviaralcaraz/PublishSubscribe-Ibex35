import java.io.Serializable;

/**
 * Created by silvia on 2/04/18.
 */
public class Alert implements Serializable{
    private String type;
    private float value;
    private CallbackClientInterface client;
    private String company;

    public Alert(String type, float value, CallbackClientInterface client, String company){
        this.type = type;
        this.value = value;
        this.client = client;
        this.company = company;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public CallbackClientInterface getClient() {
        return client;
    }

    public void setClient(CallbackClientInterface client) {
        this.client = client;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}