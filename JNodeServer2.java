import com.ericsson.otp.erlang.*;

public class JNodeServer2 {
    private OtpNode node;
    private OtpMbox mbox;

    public JNodeServer2() throws Exception {
        node = new OtpNode("j1@alpha.example.com", "secret");
        mbox = node.createMbox("java");
        OtpErlangPid pid = mbox.self();
        System.out.println("Started node: " + pid.toString());
    }

    public void process() {
        while (true) {
            try {
                OtpErlangTuple msg = (OtpErlangTuple) mbox.receive();

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
                    reply = new OtpErlangTuple(new OtpErlangObject[] { mbox.self(), new OtpErlangString("error") });
                } else {
                    reply = new OtpErlangTuple(new OtpErlangObject[] { mbox.self(), new OtpErlangInt(result) });
                }

                mbox.send(from, reply);

            } catch (OtpErlangExit | OtpErlangDecodeException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        JNodeServer2 server = new JNodeServer2();
        server.process();
    }
}
