/*******************************************************************************
 * Copyright 2020 Statement goes here
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.opensha.commons.calc.magScalingRelations.magScalingRelImpl;


import org.opensha.commons.calc.magScalingRelations.MagAreaRelationship;
import org.opensha.commons.util.FaultUtils;


/**
 * <b>Title:</b>TMG2017_MagAreaRel<br>
 *
 * <b>Description:</b>  
 * <p> 
 * This implements the set of magnitude versus rupture area relation of 
 * Thingbaijam K.K.S., P.M. Mai and K. Goda (2017, Bull. Seism. Soc. Am., 107, 2225–2246. 
 * 
 * Addition to rake, we also use regime (="crustal" or "interface") as an additional parameter 
 * to distinguish shallow crustal and subduction-interface. The default is "crustal". 
 * However, the relations for normal faults are also applicable to inslab events.   
 *  
 * Notes:
 * (1) Valid rake is in the range -180 to 180 degrees.  
 * (2) the standard deviation for area as a function of mag is given for log(area) (base-10) not area.
 * (2) The current implementation does not handle fixed seismogenic width (or saturated rupture width);
 *     It will require implementing MagLenRel and MagWidRel as well.   
 *     
 * Also see: https://github.com/thingbaijam/sceqsrc
 *    
 * </p>
 *
 * @version 0.0
 */

public class TMG2017_MagAreaRel extends MagAreaRelationship {
    /**
	 * To Do: Write something here.
	 */
	private static final long serialVersionUID = 1L;
	final static String C = "TMG2017_MagAreaRel";
    public final static String NAME = "Thingbaijam et al.(2017)";
    protected String regime = "crustal"; 
    
    
    /**
     * set regime
     * @param regime string either "crustal" or "interface"
     */
    public void setRegime(String regime) {
        FaultUtils.assertValidRegime(regime);
        this.regime = regime;
      }
    
    
    /**
     * Computes the median magnitude from rupture area for previously set rake and regime values. 
     * @param area in km
     * @return median magnitude MW
     */
    public double getMedianMag(double area){
     
      // FaultUtils.assertValidRake(rake) does not invalidate NaN - :(   
      
      if (Double.isNaN(rake))
        // return NaN because this estimate is not available.   
        return  Double.NaN; 
      
      if (regime.equalsIgnoreCase("crustal"))
      		if (( rake <= 45 && rake >= -45 ) || rake >= 135 || rake <= -135)
      			// strike slip
      			return 3.701 + 1.062*Math.log(area)*lnToLog;
      		else if (rake > 0)
      			// shallow crustal thrust/reverse faulting
      			return  4.158 + 0.953* Math.log(area)*lnToLog;
      		else
      			// normal faulting 
      			return 3.157 + 1.238*Math.log(area)*lnToLog;
      else
    	// subduction-interface
      	return 3.469 + 1.054*Math.log(area)*lnToLog;
    }
    
    
    /**
     * Gives the standard deviation for the magnitude as a function of area
     * for previously-set rake and regime values
     * @param area in km
     * @return standard deviation
     */
    public double getMagStdDev(){
      if (Double.isNaN(rake))
        // Not available
        return Double.NaN;
      
      if (regime.equalsIgnoreCase("crustal"))
    	if (( rake <= 45 && rake >= -45 ) || rake >= 135 || rake <= -135)
    	  // strike slip
    	  return  0.184;
    	else if (rake > 0)
    	  // thrust/reverse
    	  return  0.121;
    	else
    	  // normal
    	  return  0.181;
      else
    	// else subduction-interface
      	return 0.150;
    }
   
    public double getMagStdDev(Double rake,String regime) {
    	this.setRegime(regime);
    	this.setRake(rake);
    	return getMagStdDev(); 
    }

      
    /** 
     * overload to additionally consider regime 
     */
    public double getMedianMag(double area,  double rake, String regime){	 
    	this.setRegime(regime);
    	this.setRake(rake);
    	return this.getMedianMag(area);
      }
      
    
    
    /**
     * Computes the median rupture area from magnitude (for the previously set
     * rake and regime values).
     * @param mag - moment magnitude
     * @return median area in km
     */
    public double getMedianArea(double mag){
        if  (Double.isNaN(rake))
           // this estimate is not available 
           return Double.NaN;
        if (regime.equalsIgnoreCase("crustal"))
        	if (( rake <= 45 && rake >= -45 ) || rake >= 135 || rake <= -135)
        		// strike slip
        		return  Math.pow(10.0, -3.486 + 0.942*mag);
        	else if (rake > 0)
        		// shallow thrust/reverse
        		return  Math.pow(10.0, -4.362 + 1.049*mag);
        	else
        		// normal
        		return  Math.pow(10.0, -2.551 + 0.808*mag);
        else
        	//subduction-interface
        	return Math.pow(10.0, -3.292 + 0.949*mag);

    }

   
    /**
     * Computes the standard deviation of log(area) (base-10) from magnitude
     *  (for the previously set rake and regime values)
     * @return standard deviation
     */
    public double getAreaStdDev() {
    	return getMagStdDev(); 

    }

    public double getAreaStdDev(Double rake,String regime) {
    	this.setRegime(regime);
    	this.setRake(rake);
    	return getMagStdDev(); 
    }

    
    /** 
     * overload to additionally consider regime 
     */
    public double getMedianArea(double mag,  double rake, String regime){	 
    	this.setRegime(regime);
    	this.setRake(rake);
    	return this.getMedianArea(mag);
      }
      
    
    /**
     * Returns the name of the object
     *
     */
    public String getName() {
    	String type;
        if (Double.isNaN(rake))
            type = "not available";
        
        if (regime.equalsIgnoreCase("crustal"))
          if (( rake <= 45 && rake >= -45 ) || rake >= 135 || rake <= -135)
            type = "strike-slip";
          else if (rake > 0)
            type = "shallow reverse-faulting";
          else
            type =  "normal-faulting";
        else
        	type = "interface";
    	
      return NAME +" for "+type+" events";
    }
    
/*
    // A quick test; 
    // tested against sceqsrc (https://github.com/thingbaijam/sceqsrc)
    public static void main(String args[]) {
      TMG2017_MagAreaRel magRel = new TMG2017_MagAreaRel();
      
      // what happens if rake is not assigned.
      // Too bad,  FaultUtils.assertValidRake(NaN) does not throw exception;
      System.out.print(magRel.rake + "  |  " + magRel.getMedianMag(12.0) + "\n");
      
      System.out.println("Area  R_mag  N_Mag  SS_Mag Int_mag");
      System.out.print("1    ");
      System.out.print(magRel.getMedianMag(1.0, 90.0)+"    ");
      System.out.print(magRel.getMedianMag(1.0, -90.0)+"   ");
      System.out.print(magRel.getMedianMag(1.0, 0.0) + "    ");
      System.out.print(magRel.getMedianMag(1.0, 90.0, "interface")+"\n");

      magRel.setRegime("crustal");
      System.out.print("500  ");
      System.out.print(magRel.getMedianMag(500.0, 90.0)+"    ");
      System.out.print(magRel.getMedianMag(500.0, -90.0)+"   ");
      System.out.print(magRel.getMedianMag(500.0, 0.0) + "    ");
      System.out.print(magRel.getMedianMag(500.0, 90.0, "interface")+"\n");
      
      magRel.setRegime("crustal");
      System.out.print("10000  ");
      System.out.print(magRel.getMedianMag(1e4, 90.0)+"    ");
      System.out.print(magRel.getMedianMag(1e4, -90.0)+"   ");
      System.out.print(magRel.getMedianMag(1e4, 0.0) + "    ");
      System.out.print(magRel.getMedianMag(1e4, 90.0, "interface")+"\n");
      
      magRel.setRegime("crustal");
      System.out.print("100000  ");
      System.out.print(magRel.getMedianMag(1e5, 90.0)+"    ");
      System.out.print(magRel.getMedianMag(1e5, -90.0)+"   ");
      System.out.print(magRel.getMedianMag(1e5, 0.0) + "    ");
      System.out.print(magRel.getMedianMag(1e5, 90.0, "interface")+"\n");
      

      
      System.out.println(" ");
      System.out.println("Mag  R_Area N_Area  SS_Area  Int_Area");
      
      System.out.print("4 ");
      magRel.setRegime("crustal");
      System.out.print(magRel.getMedianArea(4, 90.0)+"    ");
      System.out.print(magRel.getMedianArea(4, -90.0)+"   ");
      System.out.print(magRel.getMedianArea(4, 0.0)+"    ");
      System.out.print(magRel.getMedianArea(4, 90, "interface")+"\n");
      
      System.out.print("6 ");
      magRel.setRegime("crustal");
      System.out.print(magRel.getMedianArea(6, 90.0)+"    ");
      System.out.print(magRel.getMedianArea(6, -90.0)+"   ");
      System.out.print(magRel.getMedianArea(6, 0.0)+"    ");
      System.out.print(magRel.getMedianArea(6, 90, "interface")+"\n");      
  
      
      System.out.print("8 ");
      magRel.setRegime("crustal");
      System.out.print(magRel.getMedianArea(8, 90.0)+"    ");
      System.out.print(magRel.getMedianArea(8, -90.0)+"   ");
      System.out.print(magRel.getMedianArea(8, 0.0)+"    ");
      System.out.print(magRel.getMedianArea(8, 90, "interface")+"\n");
      
      System.out.print("9 ");
      magRel.setRegime("crustal");
      System.out.print(magRel.getMedianArea(9, 90.0)+"    ");
      System.out.print(magRel.getMedianArea(9, -90.0)+"   ");
      System.out.print(magRel.getMedianArea(9, 0.0)+"    ");
      System.out.print(magRel.getMedianArea(9, 90, "interface")+"\n");
   
      System.out.println(" ");
      System.out.println("Mag_stdDev for  R_Mag  N_Mag  SS_Mag and Int_Mag:");
      magRel.setRegime("crustal");
      System.out.print(magRel.getMagStdDev(90.0)+"  ");
      System.out.print(magRel.getMagStdDev(-90.0)+"  ");
      System.out.print(magRel.getMagStdDev(0.0)+"  ");
      System.out.print(magRel.getMagStdDev(90.0, "interface")+"\n");

      System.out.println(" ");
      System.out.println("Area_stdDev for  R_Mag  N_Mag  SS_Mag and Int_Mag:");
      magRel.setRegime("crustal");
      System.out.print(magRel.getAreaStdDev(90.0)+"  ");
      System.out.print(magRel.getAreaStdDev(-90.0)+"  ");
      System.out.print(magRel.getAreaStdDev(0.0)+"  ");
      System.out.print(magRel.getAreaStdDev(90.0, "interface")+"\n");
    
    } 
    */


}
