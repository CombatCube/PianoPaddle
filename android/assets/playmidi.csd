<CsoundSynthesizer>
<CsInstruments>
;Example by Iain McCurdy

sr = 44100
ksmps = 32
nchnls = 2
0dbfs = 1

giEngine     fluidEngine                                            ; start fluidsynth engine
iSfNum1      fluidLoad          "synthgms.sf2", giEngine, 1         ; load a soundfont
             fluidProgramSelect giEngine, 1, iSfNum1, 0, 1         ; direct each midi channel to a particular soundfont
             fluidProgramSelect giEngine, 2, iSfNum1, 0, 1

  massign 0,0
  massign 1,11
  massign 2,12

  instr 11                                                           ;fluid synths for midi channels 1
    ;mididefault   60, p3 ; Default duration of 60 -- overridden by score.
    midinoteonkey p4, p5 ; Channels MIDI input to pfields.
    iKey    =    p4                                           ; read in midi note number
    iVel    =    p5                                            ; read in key velocity
    fluidNote    giEngine, 1, iKey, iVel                            ; apply note to relevant soundfont
  endin
  
  instr 12                                                           ;fluid synths for midi channels 1
    mididefault   60, p3 ; Default duration of 60 -- overridden by score.
    midinoteonkey p4, p5 ; Channels MIDI input to pfields.
    iKey    =    p4                                             ; read in midi note number
    iVel    =    p5                                            ; read in key velocity
    fluidNote    giEngine, 2, iKey, iVel                            ; apply note to relevant soundfont
  endin

  instr 99; gathering of fluidsynth audio and audio output
    iamplitude = 1
    aSigL,aSigR      fluidOut          giEngine; read all audio from the given soundfont
    outs               aSigL * iamplitude, aSigR * iamplitude; send audio to outputs
  endin

</CsInstruments>

<CsScore>
i 99 0 360; audio output instrument also keeps performance going
e
</CsScore>

</CsoundSynthesizer>