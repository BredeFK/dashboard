import {Flex, Spinner, Text} from "@radix-ui/themes";
import './Loading.css'

export default function Loading({text}: Readonly<{ text: string }>) {
    return (
        <Flex align='center' className='loading-text' direction='row' gap='3' justify='center'>
            <Text size='7' color='gray'>{text}</Text>
            <Spinner size='3'/>
        </Flex>
    )
}
