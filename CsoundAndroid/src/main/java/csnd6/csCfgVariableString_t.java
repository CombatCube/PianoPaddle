/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package csnd6;

public class csCfgVariableString_t {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected csCfgVariableString_t(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(csCfgVariableString_t obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        csndJNI.delete_csCfgVariableString_t(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setNxt(csCfgVariable_t value) {
    csndJNI.csCfgVariableString_t_nxt_set(swigCPtr, this, csCfgVariable_t.getCPtr(value), value);
  }

  public csCfgVariable_t getNxt() {
    long cPtr = csndJNI.csCfgVariableString_t_nxt_get(swigCPtr, this);
    return (cPtr == 0) ? null : new csCfgVariable_t(cPtr, false);
  }

  public void setName(SWIGTYPE_p_unsigned_char value) {
    csndJNI.csCfgVariableString_t_name_set(swigCPtr, this, SWIGTYPE_p_unsigned_char.getCPtr(value));
  }

  public SWIGTYPE_p_unsigned_char getName() {
    long cPtr = csndJNI.csCfgVariableString_t_name_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public void setP(String value) {
    csndJNI.csCfgVariableString_t_p_set(swigCPtr, this, value);
  }

  public String getP() {
    return csndJNI.csCfgVariableString_t_p_get(swigCPtr, this);
  }

  public void setType(int value) {
    csndJNI.csCfgVariableString_t_type_set(swigCPtr, this, value);
  }

  public int getType() {
    return csndJNI.csCfgVariableString_t_type_get(swigCPtr, this);
  }

  public void setFlags(int value) {
    csndJNI.csCfgVariableString_t_flags_set(swigCPtr, this, value);
  }

  public int getFlags() {
    return csndJNI.csCfgVariableString_t_flags_get(swigCPtr, this);
  }

  public void setShortDesc(SWIGTYPE_p_unsigned_char value) {
    csndJNI.csCfgVariableString_t_shortDesc_set(swigCPtr, this, SWIGTYPE_p_unsigned_char.getCPtr(value));
  }

  public SWIGTYPE_p_unsigned_char getShortDesc() {
    long cPtr = csndJNI.csCfgVariableString_t_shortDesc_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public void setLongDesc(SWIGTYPE_p_unsigned_char value) {
    csndJNI.csCfgVariableString_t_longDesc_set(swigCPtr, this, SWIGTYPE_p_unsigned_char.getCPtr(value));
  }

  public SWIGTYPE_p_unsigned_char getLongDesc() {
    long cPtr = csndJNI.csCfgVariableString_t_longDesc_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_unsigned_char(cPtr, false);
  }

  public void setMaxlen(int value) {
    csndJNI.csCfgVariableString_t_maxlen_set(swigCPtr, this, value);
  }

  public int getMaxlen() {
    return csndJNI.csCfgVariableString_t_maxlen_get(swigCPtr, this);
  }

  public csCfgVariableString_t() {
    this(csndJNI.new_csCfgVariableString_t(), true);
  }

}
