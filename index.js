const Joi = require('joi');
const express = require('express');
let bodyParser = require('body-parser')
const app = express();
const courses = [
    {id: 0, name: 'course1'},
    {id: 1, name: 'course2'},
    {id: 2, name: 'course3'}
]
app.use(bodyParser.json());

app.get('/', (req, res) => {
    res.send('Hello World!!!')
});

app.get('/api/courses', (req, res)=> {
    res.send(courses);
});

app.post('/api/courses', (req, res) => {
    console.log("req.body: ",req.body)
    const { error }  = validateCourse(req.body);
    
    if(error){
        res.status(400).send(error.details[0].message);
        return;
    }
    console.log("req: ",req.body);
    const course = {
        id: courses[courses.length-1].id+1,
        name: req.body.name
    }
    courses.push(course);
    res.send(course);
});

app.get('/api/courses/:id', (req, res)=> {
    let course = courses.find(c => c.id === parseInt(req.params.id));
    if(!course) return res.status(404).send('The course was not found');
    res.send(course);

});

app.put('/api/courses/:id', (req, res) =>{
    console.log("put: "+req.body.name);
    let course = courses.find(c => c.id === parseInt(req.params.id));
    if(!course) return res.status(404).send('The course was not found');

    
    const { error }  = validateCourse(req.body);
    
    if(error){
        res.status(400).send(error.details[0].message);
        return;
    }
    course.name = req.body.name;
    res.send(course);
});

app.delete('/api/courses/:id', (req, res) =>{
    console.log("delete: "+req.params.id);
    let course = courses.find(c => c.id === parseInt(req.params.id));
    if(!course) return res.status(404).send('The course was not found');

    const index = courses.indexOf(course);
    courses.splice(index, 1);

    res.send(course);
});

function validateCourse(course){
    const schema = {
        name: Joi.string().min(1).required()
    }

    return Joi.validate(course, schema, { allowUnknown: true });
}

const port = process.env.PORT || 3000;

app.listen(3000, () => console.log(`Listening on port ${port}...`));