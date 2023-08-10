# AWSFaceRekognition
Demo app for aws face rekognition

Detecting face liveness: https://docs.aws.amazon.com/rekognition/latest/dg/face-liveness.html <br>
CompareFaces: https://docs.aws.amazon.com/rekognition/latest/APIReference/API_CompareFaces.html
Themeing issues: https://ui.docs.amplify.aws/android/connected-components/liveness/customization

Detecting face liveness usecases: 
- By itself: Guard against bots using the platform as it requires real user input
- Can be coupled with <b>Compare Faces</b> by storing previous liveliness scan (account creation) to ensure the same user is requesting account help (account recovery)

Themeing Issues in FaceRekognition: 
- Looking through customization docs the sliding color screen is not optional and is used as part of the verification - It isn't the best experience.
- However the start view screen is optionable and we can add our own alongside theming for the facial scan screen.
- Would like the oval screen be more customizable - remove strange design choices such as the recording button on top left corner.

CompareFaces usecases:
- Needs a base profile to compare with for persona for future checks
- ID verification needs secondary layer to be verified as authentic with the amazon sdk (AWS doesn't include ID verification like persona)
- Docs state that compare faces would require a secondary human authentication to avoid false negatives - however we can set a similarity thresholds w/ usecase of auto-rejections 

The example shown below is for Detecting Face Liveness does not have any theming and does not include Compare Faces: 

https://github.com/FahadDaBest/AWSFaceRekognition/assets/135153963/9296f138-3e8f-4c93-a52c-4a179755e383

