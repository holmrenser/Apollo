package org.gmod.gbol.simpleObject.generated;


import org.gmod.gbol.simpleObject.*; 


/**
 * AnalysisFeature generated by hbm2java
 */
public abstract class AbstractAnalysisFeature extends AbstractSimpleObject implements java.io.Serializable {


     private Integer analysisFeatureId;
     private Analysis analysis;
     private Feature feature;
     private Double rawScore;
     private Double normalizedScore;
     private Double significance;
     private Double identity;

    public AbstractAnalysisFeature() {
    }

    
    public AbstractAnalysisFeature(Analysis analysis, Feature feature) {
        this.analysis = analysis;
        this.feature = feature;
    }
    public AbstractAnalysisFeature(Analysis analysis, Feature feature, Double rawScore, Double normalizedScore, Double significance, Double identity) {
       this.analysis = analysis;
       this.feature = feature;
       this.rawScore = rawScore;
       this.normalizedScore = normalizedScore;
       this.significance = significance;
       this.identity = identity;
    }
   
    public Integer getAnalysisFeatureId() {
        return this.analysisFeatureId;
    }
    
    public void setAnalysisFeatureId(Integer analysisFeatureId) {
        this.analysisFeatureId = analysisFeatureId;
    }
    public Analysis getAnalysis() {
        return this.analysis;
    }
    
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }
    public Feature getFeature() {
        return this.feature;
    }
    
    public void setFeature(Feature feature) {
        this.feature = feature;
    }
    public Double getRawScore() {
        return this.rawScore;
    }
    
    public void setRawScore(Double rawScore) {
        this.rawScore = rawScore;
    }
    public Double getNormalizedScore() {
        return this.normalizedScore;
    }
    
    public void setNormalizedScore(Double normalizedScore) {
        this.normalizedScore = normalizedScore;
    }
    public Double getSignificance() {
        return this.significance;
    }
    
    public void setSignificance(Double significance) {
        this.significance = significance;
    }
    public Double getIdentity() {
        return this.identity;
    }
    
    public void setIdentity(Double identity) {
        this.identity = identity;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
         if ( (other == null ) ) return false;
         if ( !(other instanceof AbstractAnalysisFeature) ) return false;
         AbstractAnalysisFeature castOther = ( AbstractAnalysisFeature ) other; 
         
         return ( (this.getAnalysis()==castOther.getAnalysis()) || ( this.getAnalysis()!=null && castOther.getAnalysis()!=null && this.getAnalysis().equals(castOther.getAnalysis()) ) )
 && ( (this.getFeature()==castOther.getFeature()) || ( this.getFeature()!=null && castOther.getFeature()!=null && this.getFeature().equals(castOther.getFeature()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         
         result = 37 * result + ( getAnalysis() == null ? 0 : this.getAnalysis().hashCode() );
         result = 37 * result + ( getFeature() == null ? 0 : this.getFeature().hashCode() );
         
         
         
         
         return result;
   }   

public AbstractAnalysisFeature generateClone() {
    AbstractAnalysisFeature cloned = new AnalysisFeature(); 
           cloned.analysis = this.analysis;
           cloned.feature = this.feature;
           cloned.rawScore = this.rawScore;
           cloned.normalizedScore = this.normalizedScore;
           cloned.significance = this.significance;
           cloned.identity = this.identity;
    return cloned;
}


}

