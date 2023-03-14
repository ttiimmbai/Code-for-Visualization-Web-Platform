from vmtk import pypes
from vmtk import vmtkscripts
myArguments = 'vmtkmarchingcubes -ifile myimage.vti -l 800 --pipe vmtksurfaceviewer'
myPype = pypes.PypeRun(myArguments)
mySurface = myPype.GetScriptObject('vmtkmarchingcubes','0').Surface
mySmoother = vmtkscripts.vmtkSurfaceSmoothing()
mySmoother.Surface = mySurface
mySmoother.PassBand = 0.1
mySmoother.NumberOfIterations = 30
mySmoother.Execute()
myWriter = vmtkscripts.vmtkSurfaceWriter()
myWriter.Surface = mySmoother.Surface
myWriter.OutputFileName = 'mysurface.vtp'
myWriter.Execute()