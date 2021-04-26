------------------------------- MODULE ass01 -------------------------------

EXTENDS TLC, Integers, Sequences, FiniteSets

MaxDocFilesSize == 2
MaxChunksSize == 2
TestDocFiles == { "a.pdf", "b.pdf", "c.pdf" }
TestChunks == { "alfa beta alfa gamma", "alfa beta alfa gamma", "alfa beta alfa gamma" }
DocLoaders == { "doc-loader1", "doc-loader2" }
TextAnalyzers == { "text-an1", "text-an2" }

(*--algorithm ass01a

variable 
  docFiles = <<>>,              (* docs buffer *)
  chunks = <<>>;                (* chunks buffer *)
  docDiscoverStarted = FALSE,   
  docFilesClosed = FALSE,      
  chunksClosed = FALSE,  
  numTextAnalyzersDone = 0,     (* latch for the master *)
  allDocsAnalyzed = FALSE;


(* master agent *)

fair+ process master  = "master"
begin
  p1:
    docDiscoverStarted := TRUE;     
  p2:
    await allDocsAnalyzed;
  p3:
    print "done";
end process;

(* single doc-discover agent *)

fair+ process docDiscover  = "doc-disc"
begin
  p1:
    await docDiscoverStarted;
  p2: 
    with doc \in TestDocFiles do                
        await Len(docFiles) < MaxDocFilesSize; 
        docFiles := Append(docFiles, doc);
    end with;
  p3:
    docFilesClosed := TRUE;
end process;

(* a number of doc-loader agents *)

fair+ process docLoader \in DocLoaders
variable item = "none";
begin Consume:
  while (docFiles /= <<>>) \/ (docFiles = <<>> /\ ~docFilesClosed) do
    take: 
        await (docFiles /= <<>>) \/ docFilesClosed;
        if (docFiles /= <<>>) then
            item := Head(docFiles);
            docFiles := Tail(docFiles);
          elab:
            print item;
          put:
            with ch \in TestChunks do
                await Len(chunks) < MaxChunksSize;
                chunks := Append(chunks, ch);
            end with;
        else
            skip
        end if;
  end while;
Closing:
  chunksClosed := TRUE;  
end process;

(* a number of text-analyzer agents *)
 
fair+ process textAnalyzer \in TextAnalyzers
variable item = "none";
begin Consume:
  while (chunks /= <<>>) \/ (chunks = <<>> /\ ~chunksClosed) do
    take: 
        await (chunks /= <<>>) \/ chunksClosed;
        if (chunks /= <<>>) then
            item := Head(chunks);
            chunks := Tail(chunks);
          elab:            
            print item;
        else 
          skip;
        end if; 
  end while;
NotifyEnd:
  numTextAnalyzersDone := numTextAnalyzersDone + 1;     
  if numTextAnalyzersDone = Cardinality(TextAnalyzers) then
     allDocsAnalyzed := TRUE;
  else 
     skip;     
  end if;
end process;


end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "f4206c36" /\ chksum(tla) = "25c2bf4f")
\* Label p1 of process master at line 29 col 5 changed to p1_
\* Label p2 of process master at line 31 col 5 changed to p2_
\* Label p3 of process master at line 33 col 5 changed to p3_
\* Label Consume of process docLoader at line 56 col 3 changed to Consume_
\* Label take of process docLoader at line 58 col 9 changed to take_
\* Label elab of process docLoader at line 63 col 13 changed to elab_
\* Process variable item of process docLoader at line 54 col 10 changed to item_
VARIABLES docFiles, chunks, docDiscoverStarted, docFilesClosed, chunksClosed, 
          numTextAnalyzersDone, allDocsAnalyzed, pc, item_, item

vars == << docFiles, chunks, docDiscoverStarted, docFilesClosed, chunksClosed, 
           numTextAnalyzersDone, allDocsAnalyzed, pc, item_, item >>

ProcSet == {"master"} \cup {"doc-disc"} \cup (DocLoaders) \cup (TextAnalyzers)

Init == (* Global variables *)
        /\ docFiles = <<>>
        /\ chunks = <<>>
        /\ docDiscoverStarted = FALSE
        /\ docFilesClosed = FALSE
        /\ chunksClosed = FALSE
        /\ numTextAnalyzersDone = 0
        /\ allDocsAnalyzed = FALSE
        (* Process docLoader *)
        /\ item_ = [self \in DocLoaders |-> "none"]
        (* Process textAnalyzer *)
        /\ item = [self \in TextAnalyzers |-> "none"]
        /\ pc = [self \in ProcSet |-> CASE self = "master" -> "p1_"
                                        [] self = "doc-disc" -> "p1"
                                        [] self \in DocLoaders -> "Consume_"
                                        [] self \in TextAnalyzers -> "Consume"]

p1_ == /\ pc["master"] = "p1_"
       /\ docDiscoverStarted' = TRUE
       /\ pc' = [pc EXCEPT !["master"] = "p2_"]
       /\ UNCHANGED << docFiles, chunks, docFilesClosed, chunksClosed, 
                       numTextAnalyzersDone, allDocsAnalyzed, item_, item >>

p2_ == /\ pc["master"] = "p2_"
       /\ allDocsAnalyzed
       /\ pc' = [pc EXCEPT !["master"] = "p3_"]
       /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, docFilesClosed, 
                       chunksClosed, numTextAnalyzersDone, allDocsAnalyzed, 
                       item_, item >>

p3_ == /\ pc["master"] = "p3_"
       /\ PrintT("done")
       /\ pc' = [pc EXCEPT !["master"] = "Done"]
       /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, docFilesClosed, 
                       chunksClosed, numTextAnalyzersDone, allDocsAnalyzed, 
                       item_, item >>

master == p1_ \/ p2_ \/ p3_

p1 == /\ pc["doc-disc"] = "p1"
      /\ docDiscoverStarted
      /\ pc' = [pc EXCEPT !["doc-disc"] = "p2"]
      /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, docFilesClosed, 
                      chunksClosed, numTextAnalyzersDone, allDocsAnalyzed, 
                      item_, item >>

p2 == /\ pc["doc-disc"] = "p2"
      /\ \E doc \in TestDocFiles:
           /\ Len(docFiles) < MaxDocFilesSize
           /\ docFiles' = Append(docFiles, doc)
      /\ pc' = [pc EXCEPT !["doc-disc"] = "p3"]
      /\ UNCHANGED << chunks, docDiscoverStarted, docFilesClosed, chunksClosed, 
                      numTextAnalyzersDone, allDocsAnalyzed, item_, item >>

p3 == /\ pc["doc-disc"] = "p3"
      /\ docFilesClosed' = TRUE
      /\ pc' = [pc EXCEPT !["doc-disc"] = "Done"]
      /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, chunksClosed, 
                      numTextAnalyzersDone, allDocsAnalyzed, item_, item >>

docDiscover == p1 \/ p2 \/ p3

Consume_(self) == /\ pc[self] = "Consume_"
                  /\ IF (docFiles /= <<>>) \/ (docFiles = <<>> /\ ~docFilesClosed)
                        THEN /\ pc' = [pc EXCEPT ![self] = "take_"]
                        ELSE /\ pc' = [pc EXCEPT ![self] = "Closing"]
                  /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                                  docFilesClosed, chunksClosed, 
                                  numTextAnalyzersDone, allDocsAnalyzed, item_, 
                                  item >>

take_(self) == /\ pc[self] = "take_"
               /\ (docFiles /= <<>>) \/ docFilesClosed
               /\ IF (docFiles /= <<>>)
                     THEN /\ item_' = [item_ EXCEPT ![self] = Head(docFiles)]
                          /\ docFiles' = Tail(docFiles)
                          /\ pc' = [pc EXCEPT ![self] = "elab_"]
                     ELSE /\ TRUE
                          /\ pc' = [pc EXCEPT ![self] = "Consume_"]
                          /\ UNCHANGED << docFiles, item_ >>
               /\ UNCHANGED << chunks, docDiscoverStarted, docFilesClosed, 
                               chunksClosed, numTextAnalyzersDone, 
                               allDocsAnalyzed, item >>

elab_(self) == /\ pc[self] = "elab_"
               /\ PrintT(item_[self])
               /\ pc' = [pc EXCEPT ![self] = "put"]
               /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                               docFilesClosed, chunksClosed, 
                               numTextAnalyzersDone, allDocsAnalyzed, item_, 
                               item >>

put(self) == /\ pc[self] = "put"
             /\ \E ch \in TestChunks:
                  /\ Len(chunks) < MaxChunksSize
                  /\ chunks' = Append(chunks, ch)
             /\ pc' = [pc EXCEPT ![self] = "Consume_"]
             /\ UNCHANGED << docFiles, docDiscoverStarted, docFilesClosed, 
                             chunksClosed, numTextAnalyzersDone, 
                             allDocsAnalyzed, item_, item >>

Closing(self) == /\ pc[self] = "Closing"
                 /\ chunksClosed' = TRUE
                 /\ pc' = [pc EXCEPT ![self] = "Done"]
                 /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                                 docFilesClosed, numTextAnalyzersDone, 
                                 allDocsAnalyzed, item_, item >>

docLoader(self) == Consume_(self) \/ take_(self) \/ elab_(self)
                      \/ put(self) \/ Closing(self)

Consume(self) == /\ pc[self] = "Consume"
                 /\ IF (chunks /= <<>>) \/ (chunks = <<>> /\ ~chunksClosed)
                       THEN /\ pc' = [pc EXCEPT ![self] = "take"]
                       ELSE /\ pc' = [pc EXCEPT ![self] = "NotifyEnd"]
                 /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                                 docFilesClosed, chunksClosed, 
                                 numTextAnalyzersDone, allDocsAnalyzed, item_, 
                                 item >>

take(self) == /\ pc[self] = "take"
              /\ (chunks /= <<>>) \/ chunksClosed
              /\ IF (chunks /= <<>>)
                    THEN /\ item' = [item EXCEPT ![self] = Head(chunks)]
                         /\ chunks' = Tail(chunks)
                         /\ pc' = [pc EXCEPT ![self] = "elab"]
                    ELSE /\ TRUE
                         /\ pc' = [pc EXCEPT ![self] = "Consume"]
                         /\ UNCHANGED << chunks, item >>
              /\ UNCHANGED << docFiles, docDiscoverStarted, docFilesClosed, 
                              chunksClosed, numTextAnalyzersDone, 
                              allDocsAnalyzed, item_ >>

elab(self) == /\ pc[self] = "elab"
              /\ PrintT(item[self])
              /\ pc' = [pc EXCEPT ![self] = "Consume"]
              /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                              docFilesClosed, chunksClosed, 
                              numTextAnalyzersDone, allDocsAnalyzed, item_, 
                              item >>

NotifyEnd(self) == /\ pc[self] = "NotifyEnd"
                   /\ numTextAnalyzersDone' = numTextAnalyzersDone + 1
                   /\ IF numTextAnalyzersDone' = Cardinality(TextAnalyzers)
                         THEN /\ allDocsAnalyzed' = TRUE
                         ELSE /\ TRUE
                              /\ UNCHANGED allDocsAnalyzed
                   /\ pc' = [pc EXCEPT ![self] = "Done"]
                   /\ UNCHANGED << docFiles, chunks, docDiscoverStarted, 
                                   docFilesClosed, chunksClosed, item_, item >>

textAnalyzer(self) == Consume(self) \/ take(self) \/ elab(self)
                         \/ NotifyEnd(self)

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == master \/ docDiscover
           \/ (\E self \in DocLoaders: docLoader(self))
           \/ (\E self \in TextAnalyzers: textAnalyzer(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ SF_vars(master)
        /\ SF_vars(docDiscover)
        /\ \A self \in DocLoaders : SF_vars(docLoader(self))
        /\ \A self \in TextAnalyzers : SF_vars(textAnalyzer(self))

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 

=============================================================================
\* Modification History
\* Last modified Mon Apr 26 06:37:06 CEST 2021 by aricci
\* Created Sun Mar 28 15:32:19 CEST 2021 by aricci
