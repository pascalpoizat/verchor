package cif;

import base.*;

import java.io.*;
import java.util.*;

import base.Message;
import base.Peer;
import models.base.IllegalModelException;
import models.choreography.cif.CifModel;
import models.base.IllegalResourceException;

/**
 * Created by pascalpoizat on 05/02/2014.
 * translation in Java and refactoring of Python code by the VerChor team
 */
public class CifChoreographySpecification extends ChoreographySpecification {

    /**
     * LTS generation:
     * v = new Checker()
     * c = new Choreography(filename)
     * c.computeSyncSets(true)
     * c.generateLNT()
     * c.generateSVL(false,false)
     * v.cleanAll()
     * v.generateLTS(c,false)
     * <p/>
     * Synchronizability/Realizability (supposes that LTS have been generated):
     * v = new Checker()
     * c = new Choreography(filename)
     * v.cleanSynchronizabilityResults() / v.cleanRealizabilityResults()
     * resultS = v.isSynchronizable(c,false) / resultR = v.isRealizable(c,false)
     * <p/>
     * ChoreographySpecification::computeSyncSets:
     * sets up the cone sets and synch sets of the states in this.stateMachine.states
     * <p/>
     * ChoreographySpecification::generateLNT:
     * creates an LNT file encoding the choreography specification (several processes)
     * <p/>
     * FILES
     * (given a choreography C, in a file C.cif)
     * <p/>
     * C.lnt: LNT file
     * C.svl: main SVL script (used to generate other files)
     * C_synchronizability.svl : SVL script to check for synchronizability. Generates C_synchronizability.bcg
     * C_realizability.svl : SVL script to check for realizability.
     * <p/>
     * generated by the main SVL script: (require C.lnt to work)
     * C_specification_min.bcg: reduction of the choreography specification LTS (SVL generated)
     * C_synch_composition.bcg: phase 1 reduction of the synchronous product of the projected peers (SVL generated)
     * C_synch_composition_min.bcg: phase 2 reduction of C_synch_composition.bcg (SVL generated)
     * C_asynch_composition.bcg: phase 1 reduction of the 1-bounded asynchronous product of the projected peers (SVL generated)
     * C_asynch_composition_min.bcg: phase 2 reduction of C_asynch_composition.bcg (SVL generated)
     * <p/>
     * generated by the verification scripts: (require the above SVL generated files to work)
     * C_synchronizability.bcg : counter example for synchronizability (SVL generated)
     * C_realizability.bcg : counter example for realizability (SVL generated)
     */


    public static final String NAME = "lnt based verification";
    public static final String VERSION = "1.0";

    // prefixes / suffixes for generated file contents
    private static final String synchronous_prefix = "synchro_";
    private static final String any_suffix = ":any";
    private static final String reception_message_suffix = "_REC";
    // file name parts
    private static final String synchronizability_suffix = "_synchronizability";
    private static final String realizability_suffix = "_realizability";
    private static final String minimizing_suffix = "_min";
    private static final String choreography_model_suffix = "_specification"; // was _bpmnlts
    private static final String asynchronous_composition_suffix = "_asynch_composition"; // was _acompo
    private static final String synchronous_composition_suffix = "_synch_composition"; // was _compo_sync
    // equivalences and reductions
    private static final String synchronizability_equivalence = "strong comparison using bfs";
    private static final String realizability_equivalence = "strong comparison using bfs";
    private static final String phase2_reduction_smart = "smart branching";
    private static final String phase2_reduction_no_smart = "root leaf branching";
    // commands
    private static final String EQUIVALENCE_CHECKER_COMMAND = "bisimulator";
    private static final String LTSGENERATION_COMMAND = "svl %s";
    private static final String SYNCHRONIZABILITYCHECK_COMMAND = "svl %s" + synchronizability_suffix;
    private static final String REALIZABILITYCHECK_COMMAND = "svl %s" + realizability_suffix;
    private static final String DEBUG_COMMAND = "bcg_info -labels %s";
    private static final String CLEAN_COMMAND = "svl -clean %s";
    // file suffixes
    private static final String svl_suffix = ".svl";
    private static final String lnt_suffix = ".lnt";
    private static final String bcg_suffix = ".bcg";

    // adapted
    private CifModel model;
    // own data
    private String name;
    private Behaviour behaviour;
    private HashMap<PeerId, Peer> peers;
    private HashMap<MessageId, Message> messages;

    // file related attributes
    private String userdir;
    private String choreography_model;
    private String synchronous_composition_model;
    private String asynchronous_composition_model;
    private String choreography_model_min;
    private String synchronous_composition_model_min;
    private String asynchronous_composition_model_min;
    private String synchronizability_result_model;
    private String realizability_result_model;
    private File realizability_script;
    private File synchronizability_script;
    private File general_script;
    private File lnt_file;

    public CifChoreographySpecification(CifModel model) throws IllegalModelException {
        super();
        this.model = model;
        setup();
    }

    private void setup() throws IllegalModelException {
        // builds the CifChoreographySpecification attributes from the model ones (to be used each time the model is changed)
        setupStrings();
        buildMessages();
        buildPeers();
        buildBehaviour();
    }

    @Override
    protected boolean conformsWith(HashMap<PeerId, Peer> peers) {
        return false; // NEXT RELEASE implement conformance checking
        // issue : peers cannot be CIF models. May require "combination" factories eg CIF/LTS or PNML/PNML
    }

    @Override
    protected boolean isRealizable() {
        boolean rtr = false;
        try {
            generateFilesForRealizability(false, false); // generates LNT definitions, SVL general script and realizability checking SVL script
            executeCleanAll(); // removes previous version of scripts and files generated by them
            executeGenerateLts(); // computes BCG files
            rtr = executeRealizabilityCheck(); // checks for realizability
        } catch (Exception e) { // NEXT RELEASE deal with the different exceptions
            message(e.getMessage());
        }
        return rtr;
    }

    @Override
    protected boolean isSynchronizable() {
        boolean rtr = false;
        try {
            generateFilesForSynchronizability(false, false); // generates LNT definitions, SVL general script and realizability checking SVL script
            executeCleanAll(); // removes previous version of scripts and files generated by them
            executeGenerateLts(); // computes BCG files
            rtr = executeSynchronizabilityCheck(); // checks for synchronizability
        } catch (Exception e) { // NEXT RELEASE deal with the different exceptions
            message(e.getMessage());
        }
        return rtr;
    }

    @Override
    protected HashMap<PeerId, Peer> project() {
        return null; // NEXT RELEASE implement projection
    }

    private void generateFilesForRealizability(boolean withSmartReduction, boolean generatePeers) throws IllegalResourceException, IllegalModelException {
        generateLntFile();
        generateGeneralSvlScript(withSmartReduction, generatePeers);
        generateRealizabilitySvlScript();
    }

    private void generateFilesForSynchronizability(boolean withSmartReduction, boolean generatePeers) throws IllegalResourceException, IllegalModelException {
        generateLntFile();
        generateGeneralSvlScript(withSmartReduction, generatePeers);
        generateSynchronizabilitySvlScript();
    }

    private void setupStrings() {
        // sets up strings to be used in verification for the representation of different processes (synchronous composition, asynchronous composition, etc.)
        // name of the choreography (used as a core element in all files / processes names)
        name = model.getResource().getName();
        name = name.substring(0, name.length() - (model.getSuffix().length() + 1));
        // directory to put the files on
        userdir = model.getResource().getParent();
        // model names
        choreography_model = name + choreography_model_suffix + bcg_suffix;
        synchronous_composition_model = name + synchronous_composition_suffix + bcg_suffix;
        asynchronous_composition_model = name + asynchronous_composition_suffix + bcg_suffix;
        choreography_model_min = name + choreography_model_suffix + minimizing_suffix + bcg_suffix;
        synchronous_composition_model_min = name + synchronous_composition_suffix + minimizing_suffix + bcg_suffix;
        asynchronous_composition_model_min = name + asynchronous_composition_suffix + minimizing_suffix + bcg_suffix;
        synchronizability_result_model = name + synchronizability_suffix + bcg_suffix;
        realizability_result_model = name + realizability_suffix + bcg_suffix;
        // file names
        lnt_file = new File(model.getResource().getParent(), name + lnt_suffix);
        general_script = new File(model.getResource().getParent(), name + svl_suffix);
        realizability_script = new File(model.getResource().getParent(), name + realizability_suffix + svl_suffix);
        synchronizability_script = new File(model.getResource().getParent(), name + synchronizability_suffix + svl_suffix);
        //
        message("working directory: " + userdir);
        message("name: " + name);
    }

    private void generateLntFile() throws IllegalResourceException {
        // generate LNT file from a CIF model (definition of the MAIN process, ie the one for the choreography specification)
        String script = "";
        script += generateLntDataTypes(behaviour.getAlphabet());
        // TODO
        writeToFile(script, lnt_file);
        message("LNT file generated");
    }

    private void generateGeneralSvlScript(boolean withSmartReduction, boolean generatePeers) throws IllegalResourceException, IllegalModelException {
        // generate SVL file from a CIF model (definition of the annex processes generated from the MAIN one, ie the one for the choreography specification)
        String script = "";
        String reduction;
        // select the kind of reduction to be used
        if (withSmartReduction) {
            reduction = phase2_reduction_smart;
        } else {
            reduction = phase2_reduction_no_smart;
        }
        // header
        script += "% CAESAR_OPEN_OPTIONS=\"-silent -warning\"\n% CAESAR_OPTIONS=\"-more cat\"\n\n";
        script += "% DEFAULT_PROCESS_FILE=" + name + ".lnt\n\n";
        // composition processes and their reductions
        script += String.format("\"%s\" = safety reduction of tau*.a reduction of branching reduction of\n\"MAIN [%s]\";\n\n", choreography_model_min, generateAlphabet(behaviour.getAlphabet(), false, false, false)); // NEXT RELEASE : use the table of reductions
        script += String.format("\"%s\" = %s reduction of\n%s\n", synchronous_composition_model, reduction, generateSvlSyncRedCompositional(behaviour.getAlphabet(), peers));
        script += String.format("\"%s\"= weak trace reduction of safety reduction of tau*.a reduction of branching reduction of \"%s\";\n\n", synchronous_composition_model_min, synchronous_composition_model); // NEXT RELEASE : use the table of reductions
        script += String.format("\"%s\" = %s reduction of\n%s\n", asynchronous_composition_model, reduction, generateSvlAsyncRedCompositional(behaviour.getAlphabet(), peers, true));
        script += String.format("\"%s\"= safety reduction of tau*.a reduction of branching reduction of \"%s\";\n\n", asynchronous_composition_model_min, asynchronous_composition_model); // NEXT RELEASE : use the table of reductions
        // peers
        if (generatePeers) {
            for (PeerId peer : peers.keySet()) {
                script += String.format("\"%s_peer_%s.bcg\" = safety reduction of tau*.a reduction of \"peer_%s [%s]\";\n\n", name, peer, peer,
                        generateAlphabet(behaviour.getAlphabet(), false, false, false)); // NEXT RELEASE : use the table of reductions and static String formats
                script += String.format("\"%s_apeer_%s.bcg\" = safety reduction of tau*.a reduction of \"apeer_%s [%s]\";\n\n", name, peer, peer,
                        generateAlphabet(computeDirAlphabetforPeer(peer, computePeerAlphabetForPeer(peer, behaviour.getAlphabet())), false, false, false)); // NEXT RELEASE : use the table of reductions and static String formats // NEXT RELEASE : use only computeDirAlphabetForPeer() ?
            }
        }
        writeToFile(script, general_script);
        message("general script generated");
    }

    private void generateSynchronizabilitySvlScript() throws IllegalResourceException {
        // generate SVL file from a CIF model for choregraphy verification (synchronizability check)
        // synchronizability check (WWW 2011) using trace equivalence between the synchronous composition and the 1-bounded asynchronous composition
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", synchronizability_result_model, synchronizability_equivalence, EQUIVALENCE_CHECKER_COMMAND, synchronous_composition_model, asynchronous_composition_model);
        message("synchronizability checked with: " + script);
        writeToFile(script, synchronizability_script);
    }

    private void generateRealizabilitySvlScript() throws IllegalResourceException {
        // generate SVL
        // equivalence between the choreography LTS and the distributed system LTS (async). Nb: here we only consider emissions in the distributed system
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", realizability_result_model, realizability_equivalence, EQUIVALENCE_CHECKER_COMMAND, choreography_model, asynchronous_composition_model);
        message("realizability checked with: " + script);
        writeToFile(script, realizability_script);
    }

    private void writeToFile(String contents, File file) throws IllegalResourceException {
        // writes a content to a file
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(contents);
            message("File " + file.getAbsolutePath() + " written");
            fw.close();
        } catch (IOException e) {
            throw new IllegalResourceException("Cannot open output resource");
        }
    }

    private boolean executeRealizabilityCheck() throws ExecutionException {
        execute(String.format(REALIZABILITYCHECK_COMMAND, name));
        // check if a counter example has been generated
        File f = new File(realizability_result_model);
        if (f.exists()) {
            if (isVerbose()) {
                String debugInformation = execute(String.format(DEBUG_COMMAND, realizability_result_model));
                message(debugInformation);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean executeSynchronizabilityCheck() throws ExecutionException {
        execute(String.format(SYNCHRONIZABILITYCHECK_COMMAND, name));
        // check if a counter example has been generated
        File f = new File(synchronizability_result_model);
        if (f.exists()) {
            if (isVerbose()) {
                String debugInformation = execute(String.format(DEBUG_COMMAND, synchronizability_result_model));
                message(debugInformation);
            }
            return false;
        } else {
            return true;
        }
    }

    private void executeGenerateLts() throws ExecutionException {
        execute(String.format(LTSGENERATION_COMMAND, name));
    }

    private void executeCleanAll() throws ExecutionException {
        // removes all files generated by the SVL scripts
        execute(String.format(CLEAN_COMMAND, name));
        executeCleanSynchronizabilityResults();
        executeCleanRealizabiltiyResults();
    }

    private void executeCleanSynchronizabilityResults() throws ExecutionException {
        // removes files generated by the synchronizability check
        execute(String.format(CLEAN_COMMAND, name + synchronizability_suffix));
    }

    private void executeCleanRealizabiltiyResults() throws ExecutionException {
        // removes files generated by the realizability check
        execute(String.format(CLEAN_COMMAND, name + realizability_suffix));
    }

    private String execute(String command) throws ExecutionException {
        // helper to execute an external command and return the results
        String rtr, line;
        Process p;
        BufferedReader outputs;
        rtr = "";
        try {
            message("Executing ... " + command);
            p = Runtime.getRuntime().exec(command, null, new File(userdir));
            p.waitFor();
            outputs = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = outputs.readLine()) != null) {
                rtr += line + "\n";
            }
        } catch (IOException e) {
            ExecutionException e2 = new ExecutionException("Error in executing command " + command);
            e2.setStackTrace(e.getStackTrace());
            error(e2.getMessage());
        } catch (InterruptedException e) {
            ExecutionException e2 = new ExecutionException("Error in executing command " + command);
            e2.setStackTrace(e.getStackTrace());
            error(e2.getMessage());
        }
        message(rtr);
        return rtr;

    }

    // computes behaviour adaptor
    private void buildBehaviour() {
        behaviour = new CifBehaviour(model, messages, peers);
    }

    // computes Peer adaptors
    private void buildPeers() throws IllegalModelException {
        CifPeer cifPeer;
        peers = new HashMap<PeerId, Peer>();
        for (models.choreography.cif.generated.Peer peer : model.getParticipants().getPeer()) {
            cifPeer = new CifPeer(peer);
            peers.put(cifPeer.getId(), cifPeer);
        }
    }

    // computes Message adaptors
    private void buildMessages() throws IllegalModelException {
        CifMessageAdaptor cifMessage;
        messages = new HashMap<MessageId, Message>();
        for (Object o : model.getAlphabet().getMessageOrAction()) {
            if (o instanceof models.choreography.cif.generated.Message) {
                cifMessage = new CifMessageAdaptor((models.choreography.cif.generated.Message) o);
                messages.put(cifMessage.getId(), cifMessage);
            }
        }
    }

    @Override
    public void about() {
        System.out.println(NAME + " " + VERSION);
    }

    // generates the synchronous composition of the peers of a choreography
    private String generateSvlSyncRedCompositional(Set<AlphabetElement> alphabet, HashMap<PeerId, Peer> peers) {
        String rtr = "";
        Set<AlphabetElement> synchronizationAlphabet;
        int i = 0;
        int nbPeers = peers.size();
        for (Peer peer : peers.values()) {
            if (i < nbPeers - 1) {
                rtr += "par ";
                synchronizationAlphabet = computeSynchronizationAlphabet(i, peers, alphabet);
                if (!synchronizationAlphabet.isEmpty()) {
                    rtr += generateAlphabet(synchronizationAlphabet, false, false, false);
                }
                rtr += " in\n";
            }
            rtr += String.format("peer_%s [%s]\n", peer.getId(), generateAlphabet(alphabet, false, false, false));
            i++;
            if (i < nbPeers) {
                rtr += "||\n";
            }
        }
        i = 1;
        while (i < nbPeers - 1) {
            rtr += "end par\n";
            i++;
        }
        rtr += "end par;\n";
        return rtr;
    }

    // generates the asynchronous composition of the peers of a choreography
    private String generateSvlAsyncRedCompositional(Set<AlphabetElement> alphabet, HashMap<PeerId, Peer> peers, boolean withHiding) throws IllegalModelException {
        String rtr = "";
        Set<AlphabetElement> synchronizationAlphabet, peerAlphabet, peerBufferAlphabet;
        int i = 0;
        int nbPeers = peers.size();
        for (Peer peer : peers.values()) {
            peerAlphabet = computeDirAlphabetforPeer(peer.getId(), computePeerAlphabetForPeer(peer.getId(), alphabet));
            peerBufferAlphabet = computeBothDirAlphabet(computeBufferAlphabetForPeer(peer.getId(), alphabet));
            if (i < nbPeers - 1) {
                rtr += "par ";
                synchronizationAlphabet = computeSynchronizationAlphabet(i, peers, alphabet);
                if (!synchronizationAlphabet.isEmpty()) {
                    rtr += generateAlphabet(synchronizationAlphabet, false, false, false);
                }
                rtr += " in\n";
            }
            if (withHiding) {
                if (!peerAlphabet.isEmpty()) {
                    rtr += "(total hide ";
                    rtr += generateAlphabetWithRec(peerAlphabet, false, false);
                    rtr += " in\n";
                }
            }
            rtr += String.format("peer_buffer_%s[%s---%s]", peer.getId(), generateAlphabet(peerAlphabet, false, false, false), generateAlphabet(peerBufferAlphabet, false, false, false));  // NEXT RELEASE : check if union is required or not
            if (withHiding) {
                rtr += ")\n";
            }
            i++;
            if (i < nbPeers) {
                rtr += "||\n";
            }
        }
        i = 1;
        while (i < nbPeers - 1) {
            rtr += "end par\n";
            i++;
        }
        rtr += "end par;\n";
        return rtr;
    }

    // filters an alphabet to keep only the elements relative to a peer, and adds a suffix to for the peer receptions
    // returns a new alphabet (possibly sharing alphabet elements with the original one)
    private Set<AlphabetElement> computeDirAlphabetforPeer(PeerId peer, Set<AlphabetElement> alphabet) throws IllegalModelException { // NEXT RELEASE : possibly useless method (could be done using computeBothDirAlphabet + computePeerAlphabetForPeer)
        Set<AlphabetElement> rtr = new LinkedHashSet<AlphabetElement>();
        for (AlphabetElement alphabetElement : alphabet) {
            if (alphabetElement.getInitiatingPeer().getId().equals(peer)) { // if peer is the sender of the alphabet element: keep the same alphabet element
                rtr.add(alphabetElement);
            } else
                for (Peer cifPeer : alphabetElement.getParticipants()) { // if peer is one of the receivers of the alphabet element: compute a new alphabet element with specific tagged message
                    if (cifPeer.getId().equals(peer)) {
                        Message newMessage = new CifMessage(alphabetElement.getMessage().getId() + reception_message_suffix);
                        AlphabetElement modifiedAlphabetElement = new CifAlphabetElement(newMessage, alphabetElement.getInitiatingPeer(), alphabetElement.getParticipants());
                        rtr.add(modifiedAlphabetElement);
                    }
                }
        }
        return rtr;
    }

    // filters an alphabet to keep only the elements relative to a peer
    // returns a new alphabet (possibly sharing alphabet elements with the original one)
    private Set<AlphabetElement> computePeerAlphabetForPeer(PeerId peer, Set<AlphabetElement> alphabet) {
        Set<AlphabetElement> rtr = new LinkedHashSet<AlphabetElement>();
        for (AlphabetElement alphabetElement : alphabet) {
            if (alphabetElement.getInitiatingPeer().getId().equals(peer)) { // if peer is the sender of the alphabet element: keep the same alphabet element
                rtr.add(alphabetElement);
            } else
                for (Peer cifPeer : alphabetElement.getParticipants()) { // if peer is one of the receivers of the alphabet element: compute a new alphabet element with specific tagged message
                    if (cifPeer.getId().equals(peer)) {
                        rtr.add(alphabetElement);
                    }
                }
        }
        return rtr;
    }

    // computes an alphabet with both send/receive directions from a choreography alphabet
    private Set<AlphabetElement> computeBothDirAlphabet(Set<AlphabetElement> alphabet) throws IllegalModelException {
        Set<AlphabetElement> rtr = new LinkedHashSet<AlphabetElement>();
        for (AlphabetElement alphabetElement : alphabet) {
            rtr.add(alphabetElement);
            Message newMessage = new CifMessage(alphabetElement.getMessage().getId() + reception_message_suffix);
            AlphabetElement modifiedAlphabetElement = new CifAlphabetElement(newMessage, alphabetElement.getInitiatingPeer(), alphabetElement.getParticipants());
            rtr.add(modifiedAlphabetElement);
        }
        return rtr;
    }

    // computes the alphabet used by buffers from their corresponding peer alphabet
    private Set<AlphabetElement> computeBufferAlphabetForPeer(PeerId peer, Set<AlphabetElement> alphabet) {
        Set<AlphabetElement> rtr = new LinkedHashSet<AlphabetElement>();
        for (AlphabetElement alphabetElement : alphabet) {
            if(alphabetElement.getParticipants().iterator().next().getId().equals(peer)) { // NEXT RELEASE : work with a list of participants
                rtr.add(alphabetElement);
            }
        }
        return rtr;
    }

    // ...
    private Set<AlphabetElement> computeSynchronizationAlphabet(int fromPeer, HashMap<PeerId, Peer> peers, Set<AlphabetElement> alphabet) {
        Set<AlphabetElement> rtr = new LinkedHashSet<AlphabetElement>();
        // TODO
        return rtr;
    }

    // generates a string for an alphabet
    private String generateAlphabet(Set<AlphabetElement> alphabet, boolean withAny, boolean startComma, boolean withSynchronizingMessage) {
        String rtr = "";
        int size = alphabet.size();
        int i = 0;
        Set<AlphabetElement> alphabetWork;
        if (!alphabet.isEmpty()) {
            if (withSynchronizingMessage) { // tri alphabétique
                alphabetWork = new TreeSet<AlphabetElement>(alphabet);
            } else { // ordre d'insertion
                alphabetWork = alphabet;
            }
            if (startComma) {
                rtr += ",";
            }
            for (AlphabetElement alphabetElement : alphabetWork) {
                if (withSynchronizingMessage) {
                    rtr += synchronous_prefix;
                }
                rtr += alphabetElement.toString();
                if (withAny) {
                    rtr += any_suffix;
                }
                i++;
                if (i < size) {
                    rtr += ",";
                }
            }
        }
        return rtr;
    }

    private String generateAlphabetWithRec(Set<AlphabetElement> alphabet, boolean withAny, boolean startComma) {
        String rtr = "";
        // TODO
        return rtr;
    }

    // generates the data type part of the LNT process encoding
    private String generateLntDataTypes(Set<AlphabetElement> alphabet) {
        String rtr = "";
        rtr += "type Message is\n";
        rtr += generateAlphabet(alphabet, false, false, false);
        rtr += "\n";
        rtr += "with \"==\", \"!=\"\n";
        rtr += "end type\n\n";
        rtr += "type Buffer is list of Message\n";
        rtr += "with \"==\", \"!=\"\n";
        rtr += "end type\n\n";
        rtr += "type BoundedBuffer is bbuffer (buffer: Buffer, bound: Nat)\n";
        rtr += "with \"==\", \"!=\"\n";
        rtr += "end type\n\n";
        rtr += "function insert (m: Message, q: Buffer): Buffer is\n";
        rtr += "         case q in\n";
        rtr += "         var hd: Message, tl: Buffer in\n";
        rtr += "             nil         -> return cons(m,nil)\n";
        rtr += "           | cons(hd,tl) -> return insert(m,tl)\n";
        rtr += "         end case\n";
        rtr += "end function\n\n";
        rtr += "function ishead (m: Message, q: Buffer): Bool is\n";
        rtr += "         case q in\n";
        rtr += "         var hd: Message, tl: Buffer in\n";
        rtr += "             nil         -> return false\n";
        rtr += "           | cons(hd,tl) -> return (m==hd)\n";
        rtr += "         end case\n";
        rtr += "end function\n\n";
        rtr += "function remove (q: Buffer): Buffer is\n";
        rtr += "         case q in\n";
        rtr += "         var hd: Message, tl: Buffer in\n";
        rtr += "             nil         -> return nil\n";
        rtr += "           | cons(hd,tl) -> return tl\n";
        rtr += "         end case\n";
        rtr += "end function\n\n";
        rtr += "function count (q: Buffer): Nat is\n";
        rtr += "         case q in\n";
        rtr += "         var hd: Message, tl: Buffer in\n";
        rtr += "             nil         -> return 0\n";
        rtr += "           | cons(hd,tl) -> return (1+count(tl))\n";
        rtr += "         end case\n";
        rtr += "end function\n\n";
        rtr += "function bisfull (bq: BoundedBuffer): Bool is\n";
        rtr += "  return ((count(bq.buffer))==bq.bound)\n";
        rtr += "end function\n\n";
        rtr += "function binsert (m: Message, bq: BoundedBuffer): BoundedBuffer is\n";
        rtr += "  if bisfull(bq) then\n";
        rtr += "     return bq\n";
        rtr += "  else\n";
        rtr += "     return bbuffer(insert(m,bq.buffer),bq.bound)\n";
        rtr += "  end if\n";
        rtr += "end function\n\n";
        rtr += "function bishead (m: Message, bq: BoundedBuffer): Bool is\n";
        rtr += "  return ishead(m,bq.buffer)\n";
        rtr += "end function\n\n";
        rtr += "function bremove (bq: BoundedBuffer): BoundedBuffer is\n";
        rtr += "  return bbuffer(remove(bq.buffer),bq.bound)\n";
        rtr += "end function\n\n";
        rtr += "function bcount (bq: BoundedBuffer): Nat is\n";
        rtr += "  return count(bq.buffer)\n";
        rtr += "end function\n\n";
        return rtr;
    }

}
