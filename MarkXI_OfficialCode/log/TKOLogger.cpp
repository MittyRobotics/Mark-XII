/*!!!!!!!
 * TO USE:
 * SEE HEADER FILE COMMENTS
 */
//Last edited by Vadim Korolik
//on 01/04/2014
//@author Varun Naik
#include "TKOLogger.h"

TKOLogger* TKOLogger::_instance = NULL;
/*!!!!!!!
 * TO USE:
 * SEE HEADER FILE COMMENTS
 */

TKOLogger::TKOLogger()
{
	// This should be the first singleton to be constructed
	printf("Constructing logger\n");
	_logTask = NULL;
	_logFile.open("logT.txt", ios::app); // open logT.txt in append mode
	printf("Ran file open\n");
	if (_logFile.is_open())
	{
		printf("Logfile OPEN ON BOOT!!!!\n");
	}
	struct stat filestatus;
	stat("logT.txt", &filestatus);
	printf("File: %i%s", (int)filestatus.st_size, " bytes\n");
	printf("Test1\n");
	_printSem = semMCreate(SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
	_bufSem = semMCreate(SEM_Q_PRIORITY | SEM_DELETE_SAFE | SEM_INVERSION_SAFE);
	printf("Test2\n");
//	addMessage("-------Logger booted---------");
	AddToSingletonList();

	printf("Initialized logger\n");
}

TKOLogger::~TKOLogger()
{
	// If semaphore is being used by a task when semDelete is called,
	// the task will unblock and return ERROR
	Stop();        // TODO: is this necessary?
	semDelete(_printSem);
	semDelete(_bufSem);
	if (_logTask) {
		delete _logTask;
	}
	_instance = NULL;
}

void TKOLogger::Start()
{
	// This should be the first class to be Started after enabling
	if (!_logTask) {
		_logTask = new Task("Logging", (FUNCPTR) LogRunner); // create a new task called Logging which runs LogRunner
		printf("Created logger task\n");
//		if (_logTask->SetPriority(254)) // use the constants first/wpilib provides?
//			printf("logger priority set to 254\n");
//		else
			printf("logger priority not set\n");
	}
	if (not _logFile.is_open())
		_logFile.open("logT.txt", ios::app); // open logT.txt in append mode
	if (_logFile.is_open())
		this->printMessage("Logfile OPEN!!!!\n");
	else
		this->printMessage("FILE CLOSED!!!!\n");
	this->printMessage("Logger started\n");
	_logTask->Start();
}

void TKOLogger::Stop()
{
	// This should be the last class to be Stopped after disabling
	#ifndef IGNORE_LOGGING_SEMAPHORES
	Synchronized sem(_bufSem);
	#endif
	printf("Stopping logger");
	if (_logTask && _logTask->Verify()) {
		_logTask->Stop();
	}
	if (_logFile.fail()) {
		this->printMessage("LOG FILE FAILED WHILE WRITING\n"); // TODO: is it okay to take 2 semaphores at the same time?
	} else if (!_logFile.is_open()) {
		this->printMessage("LOG FILE CLOSED WHILE WRITING\n");
	} else {
		while (_messBuffer.size() > 0) {
			_logFile << _messBuffer.front();
			_logFile << "\n";
			_messBuffer.pop();
			printf("Writing in stop %i\n", _messBuffer.size());
		}
		_logFile.flush();
		_logFile.close();
	}

	this->printMessage("Logger stopped\n");
}

void TKOLogger::LogRunner()
{
	while (true) {
		if (!_instance) {
			printf("Invalid Logger instance\n");
			break;
		}
		{
			#ifndef IGNORE_LOGGING_SEMAPHORES
			Synchronized sem(_instance->_bufSem);
			#endif
			if (_instance->_logFile.fail()) {
				_instance->printMessage("LOG FILE FAILED WHILE WRITING\n");
			} else if (!_instance->_logFile.is_open()) {
				_instance->printMessage("LOG FILE CLOSED WHILE WRITING\n");
			} else {
				if (_instance->_messBuffer.size() > 0) {
					_instance->_logFile << _instance->_messBuffer.front();
					_instance->_logFile << "\n";
					_instance->_messBuffer.pop();
				}
			}
		}
		Wait(0.025);
	}
}

TKOLogger* TKOLogger::inst()
{
	if (!_instance)
		_instance = new TKOLogger;
	return _instance;
}

int TKOLogger::getBufferLength()
{
	return _messBuffer.size();
}

void TKOLogger::addMessage(const char *format, ...)
{
	int nBytes;
	char s[_MAX_BUF_LENGTH + 1];        // Allocate extra character for '\0'
	nBytes = sprintf(s, "Time: %f     Message: ", GetClock());
	va_list args;
	va_start(args, format);
	nBytes += vsnprintf(s + nBytes, _MAX_BUF_LENGTH + 1 - nBytes, format, args);
	va_end(args);
	if (nBytes > _MAX_BUF_LENGTH) {
		nBytes = _MAX_BUF_LENGTH;
	}
	string s2(s, nBytes);
	{
		#ifndef IGNORE_LOGGING_SEMAPHORES
		Synchronized sem(_bufSem);     // TODO: make other uses of _messBuffer thread-safe with _bufSem
		#endif
		_messBuffer.push(s2);
	}
}

void TKOLogger::printMessage(const char *format, ...)
{
	char s[_MAX_BUF_LENGTH + 1];
	va_list args;
	va_start(args, format);
	vsnprintf(s, _MAX_BUF_LENGTH + 1, format, args);   // Ignore return value
	va_end(args);

	{
		#ifndef IGNORE_LOGGING_SEMAPHORES
		Synchronized sem(_printSem);
		#endif
		fputs(s, stdout);
	}
}
