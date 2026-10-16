package network.shared;
import java.io.Serializable;

public class ReplyRequest implements Serializable {
    public int parentId;
    public String text;
    public ReplyRequest(int parentId, String text) { this.parentId = parentId; this.text = text; }
}