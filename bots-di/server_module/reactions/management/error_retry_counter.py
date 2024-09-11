class ErrorRetryCounter:
    __NUM_RETRIES = 10

    def __init__(self):
        self.__retry_counter = 0
        self.__retry_key = ""

    def reset_retries(self):
        self.__retry_key = ''

    def can_retry(self, phase, house_type) -> bool:
        key = "{}-{}".format(phase, house_type)
        if self.__retry_key != key:
            self.__retry_key = key
            self.__retry_counter = 0
        self.__retry_counter += 1
        return self.__retry_counter < self.__NUM_RETRIES