import java.io.*;
import com.ericsson.otp.erlang.*;

public class JNodeClient {
    private OtpConnection conn;

    public JNodeClient() throws Exception {
        OtpSelf self = new OtpSelf("j1@alpha.example.com", "secret");
        OtpPeer peer = new OtpPeer("e1@alpha.example.com");
        conn = self.connect(peer);
        System.out.println("Started node: " + conn.self().toString());
        System.out.println("Connected to peer: " + conn.peer().toString());
    }

    public void process() {
        while (true) {
            try {
                OtpErlangTuple msg = (OtpErlangTuple) conn.receive();

                OtpErlangPid from = (OtpErlangPid) msg.elementAt(1);
                OtpErlangTuple tuple = (OtpErlangTuple) msg.elementAt(2);
                String fn = ((OtpErlangAtom) tuple.elementAt(0)).atomValue();
                int arg = (int) ((OtpErlangLong) tuple.elementAt(1)).longValue();

                System.out.println("message: {" + from.toString() + ", {" + fn + ", " + arg + "}}");

                JComplexCalculation complexCalc = new JComplexCalculation();
                Integer result = null;
                switch (fn) {
                case "foo":
                    result = complexCalc.foo(arg);
                    break;

                case "bar":
                    result = complexCalc.bar(arg);
                    break;

                default:
                }

                OtpErlangTuple reply = null;
                if (result == null) {
                    reply = new OtpErlangTuple(new OtpErlangObject[] { conn.self().pid(), new OtpErlangString("error") });
                } else {
                    reply = new OtpErlangTuple(new OtpErlangObject[] { conn.self().pid(), new OtpErlangInt(result) });
                }

                conn.send(from, reply);

            } catch (OtpErlangExit | OtpAuthException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        JNodeClient client = new JNodeClient();
        client.process();
    }
}
