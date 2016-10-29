/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package csnd6;

public class CS_MIDIDEVICE {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected CS_MIDIDEVICE(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CS_MIDIDEVICE obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        csndJNI.delete_CS_MIDIDEVICE(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setDevice_name(String value) {
    csndJNI.CS_MIDIDEVICE_device_name_set(swigCPtr, this, value);
  }

  public String getDevice_name() {
    return csndJNI.CS_MIDIDEVICE_device_name_get(swigCPtr, this);
  }

  public void setInterface_name(String value) {
    csndJNI.CS_MIDIDEVICE_interface_name_set(swigCPtr, this, value);
  }

  public String getInterface_name() {
    return csndJNI.CS_MIDIDEVICE_interface_name_get(swigCPtr, this);
  }

  public void setDevice_id(String value) {
    csndJNI.CS_MIDIDEVICE_device_id_set(swigCPtr, this, value);
  }

  public String getDevice_id() {
    return csndJNI.CS_MIDIDEVICE_device_id_get(swigCPtr, this);
  }

  public void setMidi_module(String value) {
    csndJNI.CS_MIDIDEVICE_midi_module_set(swigCPtr, this, value);
  }

  public String getMidi_module() {
    return csndJNI.CS_MIDIDEVICE_midi_module_get(swigCPtr, this);
  }

  public void setIsOutput(int value) {
    csndJNI.CS_MIDIDEVICE_isOutput_set(swigCPtr, this, value);
  }

  public int getIsOutput() {
    return csndJNI.CS_MIDIDEVICE_isOutput_get(swigCPtr, this);
  }

  public CS_MIDIDEVICE() {
    this(csndJNI.new_CS_MIDIDEVICE(), true);
  }

}