/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * verchor
 * Copyright (C) 2012-2014  Alexandre Dumont, Gwen Salaun, Matthias Gudemann for the Python version
 * Copyright (C) 2014 Pascal Poizat (@pascalpoizat) for the Python->Java refactoring
 * emails: pascal.poizat@lip6.fr
 */

package refactoring_from_python;

import models.base.IllegalModelException;
import models.base.IllegalResourceException;
import refactoring_from_python.statemachine.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// TODO check ArrayList vs HashSet vs LinkedHasSet for sets used in the approach

public class Checker {

    // prefixes / suffixes for generated file contents
    public static final String synchronous_prefix = "synchro_";
    public static final String split_prefix = "split_";
    public static final String any_suffix = ":any";
    public static final String reception_message_suffix = "_REC";
    public static final String default_branch_state_name = "default";
    // file name parts
    public static final String synchronizability_suffix = "_synchronizability";
    public static final String realizability_suffix = "_realizability";
    public static final String minimizing_suffix = "_min";
    public static final String choreography_model_suffix = "_specification"; // was _bpmnlts
    public static final String asynchronous_composition_suffix = "_asynch_composition"; // was _acompo
    public static final String synchronous_composition_suffix = "_synch_composition"; // was _compo_sync
    // equivalences and reductions
    public static final String synchronizability_equivalence = "strong comparison using bfs";
    public static final String realizability_equivalence = "strong comparison using bfs";
    public static final String phase2_reduction_smart = "smart branching";
    public static final String phase2_reduction_no_smart = "root leaf branching";
    // commands
    public static final String EQUIVALENCE_CHECKER_COMMAND = "bisimulator";
    public static final String LTSGENERATION_COMMAND = "svl %s";
    public static final String SYNCHRONIZABILITYCHECK_COMMAND = "svl %s" + synchronizability_suffix;
    public static final String REALIZABILITYCHECK_COMMAND = "svl %s" + realizability_suffix;
    public static final String DEBUG_COMMAND = "bcg_info -labels %s";
    public static final String CLEAN_COMMAND = "svl -clean %s";
    // file suffixes
    public static final String svl_suffix = ".svl";
    public static final String lnt_suffix = ".lnt";
    public static final String bcg_suffix = ".bcg";
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

    // own data
    private boolean verbose;
    private String name;
    private Map<String, String> parallelMerges;
    // model
    private Choreography choreography;

    public Checker(Choreography choreography) throws IllegalResourceException {
        this.choreography = choreography;
        this.parallelMerges = new HashMap<>();
        setupStrings();
        // note : name is initialized by setupString()
        // note : computeSyncSets() is called when loading the choreography
    }

    public Choreography getChoreography() {
        return choreography;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void message(String msg) {
        if (this.verbose) {
            System.out.println("" + msg);
        }
    }

    public void error(String msg) {
        System.out.println("ERROR: " + msg);
    }

    public void warning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    private void setupStrings() throws IllegalResourceException {
        // sets up strings to be used in verification for the representation of different processes (synchronous composition, asynchronous composition, etc.)
        // name of the choreography (used as a core element in all files / processes names)
        name = choreography.getResource().getName();
        name = name.substring(0, name.length() - (choreography.getSuffix().length() + 1));
        // directory to put the files on
        userdir = choreography.getResource().getParent();
        // model names
        choreography_model = name + Checker.choreography_model_suffix + Checker.bcg_suffix;
        synchronous_composition_model = name + Checker.synchronous_composition_suffix + Checker.bcg_suffix;
        asynchronous_composition_model = name + Checker.asynchronous_composition_suffix + Checker.bcg_suffix;
        choreography_model_min = name + Checker.choreography_model_suffix + Checker.minimizing_suffix + Checker.bcg_suffix;
        synchronous_composition_model_min = name + Checker.synchronous_composition_suffix + Checker.minimizing_suffix + Checker.bcg_suffix;
        asynchronous_composition_model_min = name + Checker.asynchronous_composition_suffix + Checker.minimizing_suffix + Checker.bcg_suffix;
        synchronizability_result_model = name + Checker.synchronizability_suffix + Checker.bcg_suffix;
        realizability_result_model = name + Checker.realizability_suffix + Checker.bcg_suffix;
        // file names
        lnt_file = new File(choreography.getResource().getParent(), name + Checker.lnt_suffix);
        general_script = new File(choreography.getResource().getParent(), name + Checker.svl_suffix);
        realizability_script = new File(choreography.getResource().getParent(), name + Checker.realizability_suffix + Checker.svl_suffix);
        synchronizability_script = new File(choreography.getResource().getParent(), name + Checker.synchronizability_suffix + Checker.svl_suffix);
        //
        message("working directory: " + userdir);
        message("name: " + name);
    }


    /**
     * dumps alphabet in LOTOS NT format to a stream
     *
     * @param alpha       alphabet
     * @param any         states if the any keywork should be used
     * @param startComma  states if a leading "," should be added (defaults to false)
     * @param syncMessage states if alphabet elements should be prefixed by the synchronization prefix (defaults to false)
     */
    public static String dumpAlphabetToStream(List<AlphabetElement> alpha, boolean any, boolean startComma, boolean syncMessage) {
        return (dumpAlphabetToString(alpha, any, startComma, syncMessage));
    }

    /**
     * dumps alphabet in LOTOS NT format to a string
     *
     * @param alpha       alphabet
     * @param any         states if the any keywork should be used
     * @param startComma  states if a leading "," should be added (defaults to false)
     * @param syncMessage states if alphabet elements should be prefixed by the synchronization prefix (defaults to false)
     * @return string representation of the alphabet in LOTOS NT
     */
    public static String dumpAlphabetToString(List<AlphabetElement> alpha, boolean any, boolean startComma, boolean syncMessage) {
        String rtr = "";
        if (alpha.size() > 0) {
            if (syncMessage) {
                alpha.sort((ae1, ae2) -> ae1.compareTo(ae2));
            }
            if (startComma) {
                rtr += ",";
            }
            rtr += alpha.stream()
                    .map(alphabetElement ->
                            String.format("%s%s%s",
                                    syncMessage ? synchronous_prefix : "",
                                    alphabetElement,
                                    any ? any_suffix : ""))
                    .collect(Collectors.joining(","));
        }
        return rtr;
    }

    /**
     * checks wether a string belongs to a list
     * TODO useless, remove and replace by native method
     *
     * @param elem
     * @param l
     * @return
     */
    public static boolean isInList(String elem, List<String> l) {
        return (l.contains(elem));
    }

    /**
     * checks whether a list is included in another list. Used only in buildChoreoFromFile to sort states
     * TODO should be refactored to simplify
     *
     * @param src        list of state ids to look for
     * @param dst        list of states
     * @param successors list of states in dst that match ids in src
     * @return indicator if successors is relevant or not
     */
    public static boolean hasSuccInList(List<String> src, List<State> dst, List<State> successors) {
        boolean resGlob = true;
        for (String succ : src) {
            boolean resLoc = false;
            for (State e : dst) {// e = instance of already encoded state
                if (succ.equals(e.getId())) {
                    successors.add(e);
                    resLoc = true;
                }
            }
            resGlob = resGlob && resLoc;
        }
        return resGlob;
    }

    /**
     * filters a list of couples (string,depth) to keep only strings where depth is 0
     *
     * @param l list of couples (string,depth)
     * @return the strings from l where the depth is 0
     */
    public static List<String> keepZeroDepthStrings(List<Couple<String, Integer>> l) {
        List<String> res = l.stream()
                .filter(couple -> couple.getSecond() == 0)
                .map(Couple::getFirst)
                .collect(Collectors.toList());
        return res;
    }

    /**
     * removes double occurences of strings in a list
     * TODO should be refactored to simplify + useless ? it seems just to return l in the end (not removing multiple occurences in l !)
     *
     * @param l    list of string to check for
     * @param full list of strings possibly with doubles
     * @return
     */
    public static List<AlphabetElement> removeDoubles(List<AlphabetElement> l, List<AlphabetElement> full) {
        List<AlphabetElement> single = new ArrayList<>();
        List<AlphabetElement> dble = new ArrayList<>();
        for (AlphabetElement name : l) {
            if (full.stream().filter(e -> e.equals(name)).count() <= 1) {
                single.add(name);
            } else {
                if (!dble.contains(name)) {
                    dble.add(name);
                }
            }
        }
        single.addAll(dble);
        return single;
    }

    /**
     * dumps a call to the successor state process
     * TODO should be refactored to remove System.out calls and to simplify (only succ[0] is used) and separate following toString/toStream pattern
     * TODO ISSUE SHOULDBETESTED wrt the use of sets / arrays
     *
     * @param alpha
     * @param succ
     * @param semic
     */
    public String dumpSucc(List<AlphabetElement> alpha, List<State> succ, boolean semic, List<String> syncSet) {
        String rtr = "";
        List<AlphabetElement> alphaSync;
        if (succ.size() > 1) {
            System.out.println("Error: only one successor expected here!");
        } else {
            if (semic) {
                rtr += (";");
            }
            State abstractState = succ.get(0);
            if (abstractState instanceof AllJoinState) {
                rtr += (" " + synchronous_prefix + abstractState.getId() + ";null\n");
            } else if (abstractState instanceof SubsetJoinState) {
                rtr += (" " + synchronous_prefix + abstractState.getId() + ";null\n");
            } else if ((abstractState instanceof AllSelectState) || (abstractState instanceof SubsetSelectState)) {
                if (splitInOtherSplitCone((GatewaySplitState)abstractState)) {
                    rtr += (abstractState.getId() + " [");
                } else {
                    rtr += (split_prefix + abstractState.getId() + " [");
                }
                rtr += dumpAlphabetToStream(alpha, false, false, false);
                // we need only the intersecting synchronization labels in the signature
                // i.e., those which are in the split and the caller process of that split
                alphaSync = abstractState.getSyncSet().stream()
                        .map(state -> state.getId()) // get ids
                        .filter(stateid -> syncSet.contains(stateid)) // keep only the ids in the intersection with syncSet
                        .map(id -> new StringAlphabetElement(id))
                        .collect(Collectors.toList());
                rtr += dumpAlphabetToStream(alphaSync, false, true, true);
                rtr += ("]\n");
            } else {
                rtr += (abstractState.getId());
                rtr += (" [");
                rtr += dumpAlphabetToStream(alpha, false, false, false);
                alphaSync = abstractState.getSyncSet().stream()
                        .map(state -> new StringAlphabetElement(state.getId()))
                        .collect(Collectors.toList());
                rtr += dumpAlphabetToStream(alphaSync, false, true, true);
                rtr += ("]\n");
            }
        }
        return rtr;
    }

    /**
     * dumps parallel composition of all messages in alphasync to a stream
     *
     * @param alphaSync
     */
    public static String dumpParallelSyncsToStream(List<AlphabetElement> alphaSync) {
        return dumpParallelSyncsToString(alphaSync);
    }

    /**
     * dumps parallel composition of all messages in alphasync to a string
     *
     * @param alphaSync
     * @return
     */
    public static String dumpParallelSyncsToString(List<AlphabetElement> alphaSync) {
        String rtr = "";
        int nb = alphaSync.size();
        if (nb == 0) {
            // TODO should raise an exception
        } else if (nb == 1) {
            rtr += String.format("%s%s; null\n", synchronous_prefix, alphaSync.get(0));
        } else {
            String parallelElements = alphaSync.stream()
                    .map(alphabetElement -> String.format("%s%s; null", synchronous_prefix, alphabetElement))
                    .collect(Collectors.joining(" || "));
            rtr += String.format("  par \n%s\n  end par\n", parallelElements);
        }
        return rtr;
    }

    /**
     * checks if a default branch exists in a list of states
     * TODO should be tested + checked that AnyMatch stops at first found
     *
     * @param lstates
     * @return
     */
    public static boolean existDefaultBranch(List<State> lstates) {
        return lstates.stream().anyMatch(s -> s.getId().equals(default_branch_state_name));
    }

    /**
     * computes the union of two lists
     * TODO should be refactored since it does not computes the set-theoretic union (if there are doubles in l2), nor the list concatenation (if there are doubles in l1), possibly useless (use native method instead)
     *
     * @param l1
     * @param l2
     * @return
     */
    public static <T> List<T> union(List<T> l1, List<T> l2) {
        List<T> l = l1.stream().filter(e -> !l2.contains(e)).collect(Collectors.toList());
        l.addAll(l2);
        return l;
    }

    /**
     * computes the intersection of two lists
     * TODO possibly useless (use native method instead)
     *
     * @param l1
     * @param l2
     * @return
     */
    public static <T> List<T> intersection(List<T> l1, List<T> l2) {
        List<T> l = l1.stream().filter(l2::contains).collect(Collectors.toList());
        return l;
    }

    /**
     * @param alphabet
     * @param withAny
     * @param startComma               defaults to false
     * @param withSynchronizingMessage defaults to false
     * @return
     */
    public static String generateAlphabet(List<AlphabetElement> alphabet, boolean withAny, boolean startComma, boolean withSynchronizingMessage) {
        String rtr = "";
        int size = alphabet.size();
        int i = 0;
        List<AlphabetElement> alphabetWork;
        if (!alphabet.isEmpty()) {
            if (withSynchronizingMessage) { // tri alphabétique
                alphabetWork = new ArrayList<>(alphabet);
                alphabetWork.sort((x, y) -> x.compareTo(y));
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

    public static String generateLntDataTypes(List<AlphabetElement> alphabet) {
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

    public void executeGenerateLts() throws ExecutionException {
        execute(String.format(Checker.LTSGENERATION_COMMAND, getChoreography().getName()), true);
    }

    public boolean executeRealizabilityCheck() throws ExecutionException { // isRealizableP
        execute(String.format(REALIZABILITYCHECK_COMMAND, getChoreography().getName()), true);
        // check if a counter example has been generated
        File f = new File(realizability_result_model);
        if (f.exists()) {
            if (isVerbose()) {
                String debugInformation = execute(String.format(Checker.DEBUG_COMMAND, realizability_result_model), true);
                message(debugInformation);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean executeSynchronizabilityCheck() throws ExecutionException { // isSynchronizableP
        execute(String.format(SYNCHRONIZABILITYCHECK_COMMAND, getChoreography().getName()), true);
        // check if a counter example has been generated
        File f = new File(synchronizability_result_model);
        if (f.exists()) {
            if (isVerbose()) {
                String debugInformation = execute(String.format(Checker.DEBUG_COMMAND, synchronizability_result_model), true);
                message(debugInformation);
            }
            return false;
        } else {
            return true;
        }
    }

    public void executeCleanUp() throws ExecutionException {
        execute(String.format(Checker.CLEAN_COMMAND, getChoreography().getName()), false);
        executeCleanResults();
    }

    public void executeCleanResults() throws ExecutionException {
        executeCleanSynchronizabilityResults();
        executeCleanRealizabiltiyResults();
    }

    public void executeCleanSynchronizabilityResults() throws ExecutionException {
        // removes files generated by the synchronizability check
        execute(String.format(Checker.CLEAN_COMMAND, getChoreography().getName() + Checker.synchronizability_suffix), false);
    }

    public void executeCleanRealizabiltiyResults() throws ExecutionException {
        // removes files generated by the realizability check
        execute(String.format(Checker.CLEAN_COMMAND, getChoreography().getName() + Checker.realizability_suffix), false);
    }

    public String execute(String command, boolean withSuccess) throws ExecutionException {
        // helper to execute an external command and return the results
        // withSuccess = true requires that the command return an exit code different from 0
        String rtr, line;
        Process p;
        BufferedReader outputs;
        rtr = "";
        try {
            message("Executing ... " + command);
            p = Runtime.getRuntime().exec(command, null, new File(userdir));
            int exitCode = p.waitFor();
            outputs = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = outputs.readLine()) != null) {
                rtr += line + "\n";
            }
            if (exitCode != 0) {
                // something went wrong during the command execution (error generated by the command)
                String errorMessage = String.format("Error in executing command %s (%d)\ntrace:\n%s", command, exitCode, rtr);
                if (withSuccess) {
                    throw new ExecutionException(errorMessage);
                } else {
                    if (isVerbose()) {
                        warning(errorMessage);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            ExecutionException e2 = new ExecutionException("Error in executing command " + command);
            e2.setStackTrace(e.getStackTrace());
            error(e2.getMessage());
        }
        message(rtr);
        return rtr;

    }

    public boolean isRealizable() {
        boolean rtr = false;
        try {
            generateFilesForRealizability(false, false); // generates LNT definitions, SVL general script and realizability checking SVL script
            executeCleanUp(); // removes previous version of scripts and files generated by them
            executeGenerateLts(); // computes BCG files
            rtr = executeRealizabilityCheck(); // checks for realizability
        } catch (Exception e) { // ISSUE ENHANCEMENT deal with the different exceptions
            message(e.getMessage());
        }
        return rtr;
    }

    protected boolean isSynchronizable() {
        boolean rtr = false;
        try {
            generateFilesForSynchronizability(false, false); // generates LNT definitions, SVL general script and realizability checking SVL script
            executeCleanUp(); // removes previous version of scripts and files generated by them
            executeGenerateLts(); // computes BCG files
            rtr = executeSynchronizabilityCheck(); // checks for synchronizability
        } catch (Exception e) { // ISSUE ENHANCEMENT deal with the different exceptions
            message(e.getMessage());
        }
        return rtr;
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

    private void generateLntFile() throws IllegalResourceException {
        // generate LNT file from a CIF model (definition of the MAIN process, ie the one for the choreography specification)
        String script = "";
        script += Checker.generateLntDataTypes(choreography.getAlphabet());
        // TODO HERE
        writeToFile(script, lnt_file);
        message("LNT file generated");
    }

    private void generateGeneralSvlScript(boolean withSmartReduction, boolean generatePeers) throws IllegalResourceException, IllegalModelException {
        // generate SVL file from a CIF model (definition of the annex processes generated from the MAIN one, ie the one for the choreography specification)
        String script = "";
        String reduction;
        // select the kind of reduction to be used
        if (withSmartReduction) {
            reduction = Checker.phase2_reduction_smart;
        } else {
            reduction = Checker.phase2_reduction_no_smart;
        }
        // header
        script += "% CAESAR_OPEN_OPTIONS=\"-silent -warning\"\n% CAESAR_OPTIONS=\"-more cat\"\n\n";
        script += "% DEFAULT_PROCESS_FILE=" + name + ".lnt\n\n";
        // composition processes and their reductions
        script += String.format("\"%s\" = safety reduction of tau*.a reduction of branching reduction of\n\"MAIN [%s]\";\n\n", choreography_model_min, Checker.generateAlphabet(choreography.getAlphabet(), false, false, false)); // ISSUE ENHANCEMENT use the table of reductions
        script += String.format("\"%s\" = %s reduction of\n%s\n", synchronous_composition_model, reduction, generateSvlSyncRedCompositional(choreography.getAlphabet(), choreography.getPeers()));
        script += String.format("\"%s\"= weak trace reduction of safety reduction of tau*.a reduction of branching reduction of \"%s\";\n\n", synchronous_composition_model_min, synchronous_composition_model); // ISSUE ENHANCEMENT use the table of reductions
        script += String.format("\"%s\" = %s reduction of\n%s\n", asynchronous_composition_model, reduction, generateSvlAsyncRedCompositional(choreography.getAlphabet(), choreography.getPeers(), true));
        script += String.format("\"%s\"= safety reduction of tau*.a reduction of branching reduction of \"%s\";\n\n", asynchronous_composition_model_min, asynchronous_composition_model); // ISSUE ENHANCEMENT use the table of reductions
        // peers
        if (generatePeers) {
            for (String peer : choreography.getPeers()) {
                script += String.format("\"%s_peer_%s.bcg\" = safety reduction of tau*.a reduction of \"peer_%s [%s]\";\n\n", name, peer, peer,
                        Checker.generateAlphabet(choreography.getAlphabet(), false, false, false)); // ISSUE ENHANCEMENT use the table of reductions and static String formats
                script += String.format("\"%s_apeer_%s.bcg\" = safety reduction of tau*.a reduction of \"apeer_%s [%s]\";\n\n", name, peer, peer,
                        Checker.generateAlphabet(computeDirAlphabetforPeer(peer, computePeerAlphabetForPeer(peer, choreography.getAlphabet())), false, false, false)); // ISSUE ENHANCEMENT use the table of reductions and static String formats // TODO ISSUE SHOULDBETESTED use only computeDirAlphabetForPeer() ?
            }
        }
        writeToFile(script, general_script);
        message("general script generated");
    }

    private void generateSynchronizabilitySvlScript() throws IllegalResourceException {
        // generate SVL file from a CIF model for choregraphy verification (synchronizability check)
        // synchronizability check (WWW 2011) using trace equivalence between the synchronous composition and the 1-bounded asynchronous composition
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", synchronizability_result_model, Checker.synchronizability_equivalence, Checker.EQUIVALENCE_CHECKER_COMMAND, synchronous_composition_model, asynchronous_composition_model);
        message("synchronizability checked with: " + script);
        writeToFile(script, synchronizability_script);
    }

    private void generateRealizabilitySvlScript() throws IllegalResourceException {
        // generate SVL
        // equivalence between the choreography LTS and the distributed system LTS (async). Nb: here we only consider emissions in the distributed system
        // important : the signalChange() method should be called each time the CIF model changes (synchronization issue)
        String script = String.format("\"%s\" = %s with %s \"%s\" ==  \"%s\";\n\n", realizability_result_model, Checker.realizability_equivalence, Checker.EQUIVALENCE_CHECKER_COMMAND, choreography_model, asynchronous_composition_model);
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

    // computes the alphabet used by buffers from their corresponding peer alphabet
    // TODO check wrt Python
    private List<AlphabetElement> computeBufferAlphabetForPeer(String peer, List<AlphabetElement> alphabet) {
        return alphabet.stream()
                .filter(element -> (element instanceof ChoreographyAlphabetElement)
                        && (((ChoreographyAlphabetElement) element).getParticipants().iterator().next().equals(peer)))
                .collect(Collectors.toList());
    }

    // filters an alphabet to keep only the elements relative to a peer, and adds a suffix to for the peer receptions
    // returns a new alphabet (possibly sharing alphabet elements with the original one)
    // TODO check wrt Python
    private List<AlphabetElement> computeDirAlphabetforPeer(String peer, List<AlphabetElement> alphabet) throws IllegalModelException { // TODO ISSUE SHOULDBETESTED possibly useless method (could be done using computeBothDirAlphabet + computePeerAlphabetForPeer)
        List<AlphabetElement> rtr = new ArrayList<>();
        for (AlphabetElement alphabetElement : alphabet) {
            if (alphabetElement instanceof ChoreographyAlphabetElement) {
                ChoreographyAlphabetElement choreographyAlphabetElement = (ChoreographyAlphabetElement) alphabetElement;
                if (choreographyAlphabetElement.getInitiatingPeer().equals(peer)) { // if peer is the sender of the alphabet element: keep the same alphabet element
                    rtr.add(choreographyAlphabetElement);
                } else
                    for (String cifPeer : choreographyAlphabetElement.getParticipants()) { // if peer is one of the receivers of the alphabet element: compute a new alphabet element with specific tagged message
                        if (cifPeer.equals(peer)) {
                            String newMessage = choreographyAlphabetElement.getMessage() + reception_message_suffix;
                            AlphabetElement modifiedAlphabetElement = new ChoreographyAlphabetElement(newMessage, choreographyAlphabetElement.getParticipants(), choreographyAlphabetElement.getInitiatingPeer());
                            rtr.add(modifiedAlphabetElement);
                        }
                    }
            }
        }
        return rtr;
    }

    // filters an alphabet to keep only the elements relative to a peer
    // returns a new alphabet (possibly sharing alphabet elements with the original one)
    // TODO check wrt Python
    private List<AlphabetElement> computePeerAlphabetForPeer(String peer, List<AlphabetElement> alphabet) {
        List<AlphabetElement> rtr = new ArrayList<>();
        for (AlphabetElement alphabetElement : alphabet) {
            if (alphabetElement instanceof ChoreographyAlphabetElement) {
                ChoreographyAlphabetElement choreographyAlphabetElement = (ChoreographyAlphabetElement) alphabetElement;
                if (choreographyAlphabetElement.getInitiatingPeer().equals(peer)) { // if peer is the sender of the alphabet element: keep the same alphabet element
                    rtr.add(choreographyAlphabetElement);
                } else
                    for (String cifPeer : choreographyAlphabetElement.getParticipants()) { // if peer is one of the receivers of the alphabet element: compute a new alphabet element with specific tagged message
                        if (cifPeer.equals(peer)) {
                            rtr.add(choreographyAlphabetElement);
                        }
                    }
            }
        }
        return rtr;
    }

    // computes an alphabet with both send/receive directions from a choreography alphabet
    // TODO check wrt Python
    private List<AlphabetElement> computeBothDirAlphabet(List<AlphabetElement> alphabet) throws IllegalModelException {
        List<AlphabetElement> rtr = new ArrayList<>();
        for (AlphabetElement alphabetElement : alphabet) {
            if (alphabetElement instanceof ChoreographyAlphabetElement) {
                ChoreographyAlphabetElement choreographyAlphabetElement = (ChoreographyAlphabetElement) alphabetElement;
                rtr.add(choreographyAlphabetElement);
                String newMessage = choreographyAlphabetElement.getMessage() + reception_message_suffix;
                AlphabetElement modifiedAlphabetElement = new ChoreographyAlphabetElement(newMessage, choreographyAlphabetElement.getParticipants(), choreographyAlphabetElement.getInitiatingPeer());
                rtr.add(modifiedAlphabetElement);
            }
        }
        return rtr;
    }

    // generates the synchronous composition of the peers of a choreography
    private String generateSvlSyncRedCompositional(List<AlphabetElement> alphabet, List<String> peers) {
        String rtr = "";
        List<AlphabetElement> synchronizationAlphabet;
        int i = 0;
        int nbPeers = peers.size();
        for (String peer : peers) {
            if (i < nbPeers - 1) {
                rtr += "par ";
                synchronizationAlphabet = computeSynchronizationAlphabet(peer, peers, alphabet);
                if (!synchronizationAlphabet.isEmpty()) {
                    rtr += Checker.generateAlphabet(synchronizationAlphabet, false, false, false);
                }
                rtr += " in\n";
            }
            rtr += String.format("peer_%s [%s]\n", peer, Checker.generateAlphabet(alphabet, false, false, false));
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
    private String generateSvlAsyncRedCompositional(List<AlphabetElement> alphabet, List<String> peers, boolean withHiding) throws IllegalModelException {
        String rtr = "";
        List<AlphabetElement> synchronizationAlphabet, peerAlphabet, peerBufferAlphabet;
        int i = 0;
        int nbPeers = peers.size();
        for (String peer : peers) {
            peerAlphabet = computeDirAlphabetforPeer(peer, computePeerAlphabetForPeer(peer, alphabet));
            peerBufferAlphabet = computeBothDirAlphabet(computeBufferAlphabetForPeer(peer, alphabet));
            if (i < nbPeers - 1) {
                rtr += "par ";
                synchronizationAlphabet = computeSynchronizationAlphabet(peer, peers, alphabet);
                if (!synchronizationAlphabet.isEmpty()) {
                    rtr += Checker.generateAlphabet(synchronizationAlphabet, false, false, false);
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
            rtr += String.format("peer_buffer_%s[%s,%s]", peer, Checker.generateAlphabet(peerAlphabet, false, false, false), Checker.generateAlphabet(peerBufferAlphabet, false, false, false));  // TODO ISSUE SHOULDBETESTED check if union is required or not
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

    private String generateAlphabetWithRec(List<AlphabetElement> alphabet, boolean withAny, boolean startComma) {
        return String.format("%s%s",
                startComma ? "," : "",
                alphabet.stream()
                        .map(alphabetElement -> String.format("%s%s%s",
                                alphabetElement,
                                reception_message_suffix,
                                withAny ? any_suffix : ""))
                        .collect(Collectors.joining(",")));
    }

    // ...
    private List<AlphabetElement> computeSynchronizationAlphabet(String fromPeer, List<String> peers, List<AlphabetElement> alphabet) {
        List<AlphabetElement> alphap = computePeerAlphabetForPeer(fromPeer, alphabet);
        List<AlphabetElement> alphas = new ArrayList<>();
        for (String k : peers) {
            List<AlphabetElement> ak = computePeerAlphabetForPeer(k, alphabet);
            alphas = union(alphas, ak);
        }
        return intersection(alphap, alphas);
    }

    public static boolean isSynchroSelect(State s) {
        return ((s instanceof AllSelectState) || (s instanceof SubsetSelectState));
    }

    public static boolean isSynchroMerge(State s) {
        return ((s instanceof AllJoinState) || (s instanceof SubsetJoinState));
    }

    /**
     * checks if there is a path from sourceState to targetState
     * TODO check wrt immutability constraint on streams + check wrt side-effect on visitedStates (reinitialized when method is called?)
     *
     * @param sourceState   source of the searched path
     * @param targetState   target of the searched path
     * @param edges         set of edges
     * @param visitedStates set of already visited states
     * @return
     */
    public static boolean existsPath(State sourceState, State targetState, Set<Couple<State, State>> edges, Set<State> visitedStates) {
        visitedStates.add(sourceState);
        return ((edges.contains(new Couple(sourceState, targetState))) ||
                (edges.stream()
                        .filter(e1 -> (e1.getFirst().equals(sourceState) && !visitedStates.contains(e1.getSecond())))
                        .anyMatch(e2 -> existsPath(e2.getSecond(), targetState, edges, visitedStates))));
    }

    /**
     * checks if a state has a merge successor
     * TODO check wrt immutability constraint on streams + check wrt side-effect on visitedStates (reinitialized when method is called?)
     *
     * @param sourceState   state
     * @param edges         set of edges
     * @param visitedStates set of already visited states
     * @return
     */
    public static boolean existsMergeSuccessor(State sourceState, Set<Couple<State, State>> edges, Set<State> visitedStates) {
        visitedStates.add(sourceState);
        return ((sourceState.getSuccessors().stream()
                .anyMatch(s -> isSynchroMerge(s))) ||
                (edges.stream()
                        .filter(e1 -> (e1.getFirst().equals(sourceState) && !visitedStates.contains(e1.getSecond())))
                        .anyMatch(e2 -> existsMergeSuccessor(e2.getSecond(), edges, visitedStates))));
    }

    /**
     * checks if sourceState has a merge state targetState reachable from it, without any merge state in-between
     * TODO check wrt immutability constraint on streams + check wrt side-effect on visitedStates (reinitialized when method is called?)
     *
     * @param sourceState   state
     * @param targetState   target state
     * @param edges         set of edges
     * @param visitedStates set of already visited states
     * @return
     */
    public static boolean successorNoMergeBetween(State sourceState, State targetState, Set<Couple<State, State>> edges, Set<State> visitedStates) {
        visitedStates.add(sourceState);
        return (((!isSynchroMerge(sourceState)) && (edges.contains(new Couple(sourceState, targetState)))) ||
                (edges.stream()
                        .filter(e1 -> (e1.getFirst().equals(sourceState) && !visitedStates.contains(e1.getSecond()) && !isSynchroMerge(e1.getSecond())))
                        .anyMatch(e2 -> successorNoMergeBetween(e2.getSecond(), targetState, edges, visitedStates))));
    }

    /**
     * performs a simple faulty check
     * all interaction states directly following a choice state should have the same initiator
     * TODO could be simplified and should be tested
     *
     * @return
     */
    public boolean simpleFaultyP() {
        return getChoreography().getStates().stream()
                .filter(state -> state instanceof ChoiceState)
                .allMatch(choiceState -> {
                    Set<State> interactionStates = choiceState.getSuccessors().stream()
                            .filter(s -> s instanceof InteractionState)
                            .collect(Collectors.toSet());
                    if (!interactionStates.isEmpty()) {
                        boolean result = true;
                        String initiator;
                        initiator = ((InteractionState) interactionStates.iterator().next()).getInitiator();
                        for (State interactionState : interactionStates) {
                            result = result && (((InteractionState) interactionState).getInitiator().equals(initiator));
                        }
                        return !result;
                    } else {
                        return true;
                    }
                });
    }

    public boolean splitInOtherSplitCone(GatewaySplitState splitState) {
        List<State> filteredStates;
        if (splitState instanceof refactoring_from_python.statemachine.AllSelectState) {
            filteredStates = getChoreography().getStates().stream()
                    .filter(state -> (!state.equals(splitState) && (state instanceof AllSelectState)))
                    .collect(Collectors.toList());
        } else if (splitState instanceof SubsetSelectState) {
            filteredStates = getChoreography().getStates().stream()
                    .filter(state -> (!state.equals(splitState) && (state instanceof SubsetSelectState)))
                    .collect(Collectors.toList());
        } else {
            filteredStates = getChoreography().getStates().stream()
                    .filter(state -> (!state.equals(splitState) && Checker.isSynchroSelect(state)))
                    .collect(Collectors.toList());
        }
        return filteredStates.stream().anyMatch(state -> ((GatewaySplitState) state).getConeSet().contains(splitState));
    }

    public void computeSyncSets() {
        Set<Couple<State, State>> edgeSet = getChoreography().getInitialState().getEdges(new HashSet<>());
        for (State s : getChoreography().getStates()) {
            if (Checker.isSynchroSelect(s)) {
                Set<State> coneSet = ((GatewaySplitState) s).computeConeSet(edgeSet, getChoreography().getStates());
                ((GatewaySplitState) s).setConeSet(coneSet);
            }
        }
        List<State> splitStates = getChoreography().getStates().stream()
                .filter(state -> Checker.isSynchroSelect(state))
                .collect(Collectors.toList());
        List<State> mergeStates = getChoreography().getStates().stream()
                .filter(state -> Checker.isSynchroMerge(state))
                .collect(Collectors.toList());
        for (State mergeState : mergeStates) {
            mergeState.getSyncSet().add(mergeState);
            for (State splitState : splitStates) {
                Set<State> cone = ((GatewaySplitState)splitState).getConeSet();
                for (State s : getChoreography().getStates()) {
                    if ((cone.contains(mergeState)) && (cone.contains(s))) {
                        if (Checker.successorNoMergeBetween(s, mergeState, edgeSet, new HashSet<>())) {
                            s.getSyncSet().add(mergeState);
                        }
                    }
                }
            }
        }
    }




}
