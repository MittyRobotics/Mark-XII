//Last edited by Vadim Korolik
//on 11/07/2013
#include "VisionFunc.h"

VisionFunc* VisionFunc::m_Instance = NULL;

VisionFunc::VisionFunc()
{
	AddToSingletonList();
}
VisionFunc::~VisionFunc()
{
	m_Instance = NULL;
}
VisionFunc* VisionFunc::inst()
{
	if (!m_Instance)
	{
		printf("VisionFunc instance is null\n");
		m_Instance = new VisionFunc;
	}
	return m_Instance;
}
/**
 * Computes the estimated distance to a target using the height of the particle in the image. For more information and graphics
 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
 * 
 * @param image The image to use for measuring the particle estimated rectangle
 * @param report The Particle Analysis Report for the particle
 * @param outer True if the particle should be treated as an outer target, false to treat it as a center target
 * @return The estimated distance to the target in Inches.
 */
double VisionFunc::computeDistance (BinaryImage *image, ParticleAnalysisReport *report) {
	double rectLong, height;
	double targetHeight;
	int divToInches = 1;
	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall
	//OUR GHETTO: Vertical reflector 4" wide x 18" tall, horizontal 24" wide x 4" tall
	imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE, &rectLong);
	//using the smaller of the estimated rectangle long side and the bounding rectangle height results in better performance
	//on skewed rectangles
	height = min(report->boundingRect.height, rectLong); //what is rectLong????
	//i guess it is the length of the particle (verticle rect) in the image, as calculated by imaq and NOT by the boundingRect...
	targetHeight = 17.75; //SO THIS IS HEIGHT OF VERTICAL RECTANGLE???
	printf("TarHeight: %f\n", height);
	return Y_IMAGE_RES * targetHeight / (height * divToInches * 2 * tan(VIEW_ANGLE*PI/(180*2))); // change divToInches to 12 to return distance in feet
}

/**
 * Computes a score (0-100) comparing the aspect ratio to the ideal aspect ratio for the target. This method uses
 * the equivalent rectangle sides to determine aspect ratio as it performs better as the target gets skewed by moving
 * to the left or right. The equivalent rectangle is the rectangle with sides x and y where particle area= x*y
 * and particle perimeter= 2x+2y
 * 
 * @param image The image containing the particle to score, needed to perform additional measurements
 * @param report The Particle Analysis Report for the particle, used for the width, height, and particle number
 * @param outer	Indicates whether the particle aspect ratio should be compared to the ratio for the inner target or the outer
 * @return The aspect ratio score (0-100)
 */
double VisionFunc::scoreAspectRatio(BinaryImage *image, ParticleAnalysisReport *report, bool vertical){
	double rectLong, rectShort, idealAspectRatio, aspectRatio;
	idealAspectRatio = vertical ? (4.0/18.0) : (24.0/4.0);	
	//TODO Change to real values ^^^^
	//Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall
	//OUR GHETTO: Vertical reflector 4" wide x 18" tall, horizontal 24" wide x 4" tall
	imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE, &rectLong);
	imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE, &rectShort);
	
	//Divide width by height to measure aspect ratio
	if(report->boundingRect.width > report->boundingRect.height){
		//particle is wider than it is tall, divide long by short
		aspectRatio = ratioToScore(((rectLong/rectShort)/idealAspectRatio));
	} else {
		//particle is taller than it is wide, divide short by long
		aspectRatio = ratioToScore(((rectShort/rectLong)/idealAspectRatio));
	}
	return aspectRatio;		//force to be in range 0-100
}

/**
 * Compares scores to defined limits and returns true if the particle appears to be a target
 * 
 * @param scores The structure containing the scores to compare
 * @param vertical True if the particle should be treated as a vertical target, false to treat it as a horizontal target
 * 
 * @return True if the particle meets all limits, false otherwise
 */
bool VisionFunc::scoreCompare(Scores scores, bool vertical){
	bool isTarget = true;

	isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
	if(vertical){
		isTarget &= scores.aspectRatioVertical > ASPECT_RATIO_LIMIT;
	} else {
		isTarget &= scores.aspectRatioHorizontal > ASPECT_RATIO_LIMIT;
	}

	return isTarget;
}

/**
 * Computes a score (0-100) estimating how rectangular the particle is by comparing the area of the particle
 * to the area of the bounding box surrounding it. A perfect rectangle would cover the entire bounding box.
 * 
 * @param report The Particle Analysis Report for the particle to score
 * @return The rectangularity score (0-100)
 */
double VisionFunc::scoreRectangularity(ParticleAnalysisReport *report){
	if(report->boundingRect.width*report->boundingRect.height !=0){
		return 100*report->particleArea/(report->boundingRect.width*report->boundingRect.height);
	} else {
		return 0;
	}	
}	

/**
 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
 */
double VisionFunc::ratioToScore(double ratio)
{
	return (max(0, min(100*(1-fabs(1-ratio)), 100)));
}

/**
 * Takes in a report on a target and compares the scores to the defined score limits to evaluate
 * if the target is a hot target or not.
 * 
 * Returns True if the target is hot. False if it is not.
 */
bool VisionFunc::hotOrNot(TargetReport target)
{
	bool isHot = true;
	
	isHot &= target.tapeWidthScore >= TAPE_WIDTH_LIMIT;
	isHot &= target.verticalScore >= VERTICAL_SCORE_LIMIT;
	isHot &= (target.leftScore > LR_SCORE_LIMIT) | (target.rightScore > LR_SCORE_LIMIT);
	
	return isHot;
}
